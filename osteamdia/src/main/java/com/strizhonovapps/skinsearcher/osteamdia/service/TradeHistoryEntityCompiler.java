package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.strizhonovapps.skinsearcher.osteamdia.entity.*;
import com.strizhonovapps.skinsearcher.osteamdia.model.*;
import com.strizhonovapps.skinsearcher.osteamdia.repository.AppliedStickerStateRepository;
import com.strizhonovapps.skinsearcher.osteamdia.repository.SkinRepository;
import com.strizhonovapps.skinsearcher.osteamdia.repository.StickerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeHistoryEntityCompiler {

    private static final String STICKER_PREFIX = "Sticker | ";
    private static final String SOUVENIR_TAG = "Souvenir";
    private static final String SKIN_NAME_PATTERN = "^.+\\s\\|\\s(.+\\s)+\\((Factory New|Minimal Wear|Field-Tested|Well-Worn|Battle-Scarred)\\)$";

    @Value("${app.skipSouvenirStickers:true}")
    private final Boolean skipSouvenir;
    private final CsGoFloatRetriever csGoFloatRetriever;
    private final HistoryPriceProvider historyPriceProvider;
    private final HoverParser hoverParser;
    private final AppliedStickerStateRepository appliedStickerStateRepository;
    private final SkinRepository skinRepository;
    private final StickerRepository stickerRepository;

    private final Lock lock = new ReentrantLock();

    public Stream<TradeHistoryEntity> compileEntities(TradeHistoryIntermediateDataChunk dataChunk) {
        Map<String, String> idsForHistoryRow = hoverParser.parseHovers(dataChunk.getHovers());
        return dataChunk.getTradeItems().parallelStream()
                .map(tradeItem -> createIfPossible(tradeItem, dataChunk.getAssetItems(), idsForHistoryRow))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<TradeHistoryEntity> createIfPossible(TradeItem tradeItem,
                                                          List<AssetItem> assetItems,
                                                          Map<String, String> idsForHistoryRow) {
        return findCsGoDecentItem(tradeItem, assetItems, idsForHistoryRow)
                .map(assetItem -> toTradeHistoryEntity(assetItem, tradeItem));
    }

    private TradeHistoryEntity toTradeHistoryEntity(AssetItem assetItem, TradeItem tradeItem) {
        SkinEntity skinEntity = getOrCreate(assetItem);
        CsGoFloatAssetResponse floatResponse = csGoFloatRetriever.getFloatResponse(assetItem.getSteamLink());
        Integer marketPriceOnSellDate = Optional.ofNullable(tradeItem.getDateCompleted())
                .flatMap(dateCompleted -> historyPriceProvider.findPrice(assetItem.getName(), dateCompleted))
                .orElse(null);
        Collection<AppliedStickerState> stickers = Boolean.TRUE.equals(skipSouvenir) && assetItem.getName().contains(SOUVENIR_TAG)
                ? new ArrayList<>()
                : mapStickers(tradeItem, floatResponse);
        return TradeHistoryEntity.builder()
                .skinEntity(skinEntity)
                .skinFloat(floatResponse.getFloatValue())
                .nameTag(assetItem.getNameTag())
                .operation(tradeItem.getTradeOperation())
                .dateCreated(tradeItem.getDateCreated())
                .dateCompleted(tradeItem.getDateCompleted())
                .priceOnTradeDate(marketPriceOnSellDate)
                .sellPrice(tradeItem.getPrice())
                .build()
                .addStickers(stickers);
    }

    private Collection<AppliedStickerState> mapStickers(TradeItem tradeItem, CsGoFloatAssetResponse floatResponse) {
        return floatResponse.getAppliedStickers().stream()
                .map(sticker -> this.map(sticker, tradeItem.getDateCompleted()))
                .toList();
    }

    private AppliedStickerState map(AppliedStickerDto currentSticker, LocalDate sellDate) {
        Integer price = Optional.ofNullable(sellDate)
                .flatMap(nonNullSellDate -> historyPriceProvider.findPrice(STICKER_PREFIX + currentSticker.getName(), nonNullSellDate))
                .orElse(null);
        lock.lock();
        try {
            return getOrCreate(currentSticker, price);
        } finally {
            lock.unlock();
        }
    }

    private AppliedStickerState getOrCreate(AppliedStickerDto currentSticker, @Nullable Integer price) {
        return appliedStickerStateRepository.find(
                        currentSticker.getName(),
                        currentSticker.getWearPercentage(),
                        price
                )
                .orElseGet(() -> {
                            AppliedStickerState newSticker = AppliedStickerState.builder()
                                    .stickerEntity(getOrCreate(currentSticker.getName()))
                                    .wear(currentSticker.getWearPercentage())
                                    .priceOnTradeDate(price)
                                    .build();
                            return appliedStickerStateRepository.saveAndFlush(newSticker);
                        }
                );
    }

    private StickerEntity getOrCreate(String stickerName) {
        return stickerRepository.findByName(stickerName)
                .orElseGet(
                        () -> stickerRepository.saveAndFlush(
                                StickerEntity.builder()
                                        .name(stickerName)
                                        .build()
                        )
                );
    }

    private SkinEntity getOrCreate(AssetItem assetItem) {
        return skinRepository.findBySkinName(assetItem.getName())
                .orElseGet(() -> skinRepository.saveAndFlush(SkinEntity.builder()
                        .skinName(assetItem.getName())
                        .wear(SkinWear.from(assetItem.getName()))
                        .build()));
    }

    private Optional<AssetItem> findCsGoDecentItem(TradeItem tradeItem,
                                                   List<AssetItem> assetItems,
                                                   Map<String, String> idsForHistoryRow) {
        String historyCode = tradeItem.getHistoryCode();
        String id = idsForHistoryRow.get(historyCode);
        return assetItems.stream()
                .filter(assetItem -> assetItem.getId().equals(id))
                .filter(assetItem -> assetItem.getCommodity().equals(0))
                .filter(assetItem -> assetItem.getSteamLink() != null)
                .filter(assetItem -> assetItem.getName().matches(SKIN_NAME_PATTERN))
                .findFirst();
    }

}
