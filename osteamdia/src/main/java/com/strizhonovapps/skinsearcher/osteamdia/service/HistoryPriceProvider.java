package com.strizhonovapps.skinsearcher.osteamdia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryPriceProvider {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd yyy");
    private final Pattern pattern = Pattern.compile("[\\s\\S]+var line1\\s?=\\s?\\[(.+)];[\\s\\S]+");

    private final UrlProvider urlProvider;
    private final CachingMarketItemPageProvider pricePageProvider;

    public Optional<Integer> findPrice(String itemName, LocalDate sellDate) {
        URI uri = urlProvider.getUrlForHistoryPrice(itemName);
        String htmlResponse = pricePageProvider.getMarketItemHtmlPage(uri);
        return findPrice(itemName, sellDate, htmlResponse);
    }

    private Optional<Integer> findPrice(String itemName, LocalDate sellDate, String htmlResponse) {
        Matcher matcher = pattern.matcher(htmlResponse);
        boolean found = matcher.find();
        if (!found) {
            throw new IllegalStateException("Unable to find historical data for " + itemName);
        }

        String historicalPricesData = matcher.group(1);
        String[] historicalPriceItems = historicalPricesData.split("], ?\\[");
        List<Map.Entry<LocalDate, Integer>> datesAndPrices = Arrays.stream(historicalPriceItems)
                .map(this::toEntryOfDateAndPrice)
                .filter(entry -> entry.getKey().isBefore(sellDate) || entry.getKey().isEqual(sellDate))
                .toList();
        LocalDate maxExistingDateBeforeSellDate = datesAndPrices.stream()
                .map(Map.Entry::getKey)
                .max(Comparator.comparing(Function.identity()))
                .orElseThrow();
        OptionalDouble priceForDate = datesAndPrices
                .stream().filter(entry -> entry.getKey().equals(maxExistingDateBeforeSellDate))
                .map(Map.Entry::getValue)
                .mapToInt(Integer::intValue)
                .average();
        if (priceForDate.isEmpty()) {
            return Optional.empty();
        }
        int actualPrice = BigDecimal.valueOf(priceForDate.getAsDouble()).setScale(0, RoundingMode.HALF_UP).intValue();
        return Optional.of(actualPrice);
    }

    private Map.Entry<LocalDate, Integer> toEntryOfDateAndPrice(String priceItem) {
        String[] itemElements = priceItem.replace("[", "")
                .replace("]", "")
                .split(",");
        String dateSrc = itemElements[0].substring(1, 12);
        LocalDate localDate = LocalDate.parse(dateSrc, dateTimeFormatter);
        int priceUsdCents = FinanceUtils.toCents(itemElements[1]);
        return Map.entry(localDate, priceUsdCents);
    }

}
