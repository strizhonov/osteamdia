package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.strizhonovapps.skinsearcher.osteamdia.entity.TradeHistoryEntity;
import com.strizhonovapps.skinsearcher.osteamdia.model.*;
import com.strizhonovapps.skinsearcher.osteamdia.repository.TradeHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCoreService implements Runnable {

    private static final int STEAM_DEFAULT_MAX_PAGE_SIZE = 100;

    @Value("${app.secure.steam}")
    private final String steamLoginSecure;
    @Value("${app.maxPageIdxToProcess:#{null}}")
    private final Integer maxPageIdxToProcess;
    private final TradeHistoryRetriever tradeHistoryRetriever;
    private final Supplier<TradeItemsParser> parserSupplier;
    private final TradeHistoryRepository tradeHistoryRepository;
    private final TradeHistoryEntityCompiler tradeHistoryEntityCompiler;

    private final Lock lock = new ReentrantLock();

    @Override
    public void run() {
        getSequenceDependentData(steamLoginSecure)
                .parallelStream()
                .flatMap(tradeHistoryEntityCompiler::compileEntities)
                .forEach(this::saveIfNotExists);
        tradeHistoryRepository.flush();
    }

    private List<TradeHistoryIntermediateDataChunk> getSequenceDependentData(String steamLoginSecure) {
        List<SteamHistoryResponseWrapper> historyResponses = getHistoryResponses(steamLoginSecure);
        log.info("Got {} history responses", historyResponses.size());

        TradeItemsParser tradeItemsParser = parserSupplier.get();
        return historyResponses.stream()
                .map(response -> parseOnePage(response, tradeItemsParser))
                .toList();
    }

    public TradeHistoryIntermediateDataChunk parseOnePage(SteamHistoryResponseWrapper historyResponse,
                                                          TradeItemsParser parser) {
        log.info("Parsing {} page.", historyResponse.getPage());
        List<TradeItem> tradeItems = parser.parse(historyResponse.getResultHtml());
        return TradeHistoryIntermediateDataChunk.builder()
                .tradeItems(tradeItems)
                .assetItems(historyResponse.getAssetItems())
                .hovers(historyResponse.getHovers())
                .build();
    }

    private void saveIfNotExists(TradeHistoryEntity entity) {
        lock.lock();
        try {
            log.info("Trying to save entity {}", entity);
            if (Boolean.FALSE.equals(tradeHistoryRepository.exists(entity))) {
                tradeHistoryRepository.saveAndFlush(entity);
            }
        } finally {
            lock.unlock();
        }
    }

    private List<SteamHistoryResponseWrapper> getHistoryResponses(String steamLoginSecure) {
        log.info("Retrieving initial history response.");

        SteamHistoryResponseWrapper firstPage = tradeHistoryRetriever.getHistoryResponse(
                steamLoginSecure,
                0,
                STEAM_DEFAULT_MAX_PAGE_SIZE,
                0
        );

        Integer totalPagesCount = firstPage.getTotalCount();
        int pagesLeft = getPagesNum(totalPagesCount) - 1;
        log.info("Total items count: {}", totalPagesCount);

        Stream<SteamHistoryResponseWrapper> nextPages = IntStream.range(1, pagesLeft + 1)
                .parallel()
                .mapToObj(pageIdx -> {
                    log.info("Getting history response for page: {}", pageIdx);

                    int start = pageIdx * STEAM_DEFAULT_MAX_PAGE_SIZE;
                    return tradeHistoryRetriever.getHistoryResponse(
                            steamLoginSecure,
                            start,
                            STEAM_DEFAULT_MAX_PAGE_SIZE,
                            pageIdx
                    );
                });

        return Stream
                .concat(Stream.of(firstPage), nextPages)
                .sorted(Comparator.comparing(SteamHistoryResponseWrapper::getPage))
                .toList();
    }

    private int getPagesNum(int totalCount) {
        Integer actualMaxItemIdxToProcess = Optional.ofNullable(this.maxPageIdxToProcess)
                .map(pageIdx -> (pageIdx + 1) * STEAM_DEFAULT_MAX_PAGE_SIZE)
                .map(maxItemToProcess -> Math.min(totalCount, maxItemToProcess))
                .orElse(totalCount);
        return (int) Math.ceil(1.0 * actualMaxItemIdxToProcess / STEAM_DEFAULT_MAX_PAGE_SIZE);
    }

}
