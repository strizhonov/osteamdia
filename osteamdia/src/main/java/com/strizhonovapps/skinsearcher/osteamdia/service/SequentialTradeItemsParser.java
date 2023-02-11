package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.strizhonovapps.skinsearcher.osteamdia.model.TradeItem;
import org.jsoup.nodes.Element;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * No year data provided by steam-api, only date-month. So this parser may be used if your steam history doesn't
 * contain year gaps. In other words - if you have at least one trade per each year - dates will be set correctly.
 * Otherwise, year of the listing may be shifted.
 */
public class SequentialTradeItemsParser extends TradeItemsParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy");

    private final AtomicInteger year;
    private final AtomicReference<LocalDate> latestDate = new AtomicReference<>();

    public SequentialTradeItemsParser(@Nullable Integer latestTradeYear) {
        Integer actualLatestTradeYear = Optional.ofNullable(latestTradeYear).orElse(LocalDate.now().getYear());
        year = new AtomicInteger(actualLatestTradeYear);
    }

    @Override
    protected TradeItem toListingItem(Element listingElement) {
        TradeItem item = new TradeItem();
        item.setHistoryCode(listingElement.id());

        Element listingCompletedDateElement = listingElement.getElementsByClass("market_listing_listed_date").get(0);
        LocalDate dateCompleted = processAndGetDateCompleted(listingCompletedDateElement);
        item.setDateCompleted(dateCompleted);

        Element listingCreatedDateElement = listingElement.getElementsByClass("market_listing_listed_date").get(1);
        LocalDate dateCreated = getDateCreated(dateCompleted, listingCreatedDateElement);
        item.setDateCreated(dateCreated);

        inflateBaseListingData(listingElement, item);

        if (item.getTradeOperation() == TradeItem.TradeOperation.SOLD) {
            addFeeToPrice(item);
        }

        return item;
    }

    private LocalDate getDateCreated(LocalDate dateCompleted, Element listingCreatedDateElement) {
        String dateSrc = listingCreatedDateElement.ownText();
        if (dateSrc.isBlank()) {
            return null;
        }
        LocalDate dateCreated = LocalDate.parse(
                dateSrc + " " + year.get(),
                DATE_FORMATTER
        );
        return dateCompleted != null && dateCreated.isAfter(dateCompleted)
                ? dateCreated.minusYears(1)
                : dateCreated;
    }

    private LocalDate processAndGetDateCompleted(Element listingCompletedDateElement) {
        String dateSrc = listingCompletedDateElement.ownText();
        if (dateSrc.isBlank()) {
            return null;
        }
        LocalDate dateCompleted = LocalDate.parse(
                dateSrc + " " + year.get(),
                DATE_FORMATTER
        );
        if (latestDate.get() != null && dateCompleted.isAfter(latestDate.get())) {
            year.getAndDecrement();
            dateCompleted = dateCompleted.minusYears(1);
        }
        latestDate.set(dateCompleted);
        return dateCompleted;
    }

}
