package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.strizhonovapps.skinsearcher.osteamdia.model.TradeItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Optional;

public class TradeItemsParser {

    public List<TradeItem> parse(String html) {
        Document doc = Jsoup.parse(html);
        Elements divs = doc.body().children().get(0).children().get(0).children();
        return divs.stream()
                .filter(div -> div.hasClass("market_listing_row"))
                .map(this::toListingItem)
                .toList();
    }

    protected TradeItem toListingItem(Element listingElement) {
        TradeItem item = new TradeItem();
        item.setHistoryCode(listingElement.id());
        inflateBaseListingData(listingElement, item);
        if (item.getTradeOperation() == TradeItem.TradeOperation.SOLD) {
            addFeeToPrice(item);
        }

        return item;
    }

    protected void inflateBaseListingData(Element listingElement, TradeItem item) {
        Elements listingParts = listingElement.children();
        for (Element listingPartElement : listingParts) {
            if (listingPartElement.hasClass("market_listing_item_name_block")) {
                String name = getItemName(listingPartElement);
                item.setName(name);
            } else if (listingPartElement.hasClass("market_listing_gainorloss")) {
                TradeItem.TradeOperation tradeOperation = getTradeOperation(listingPartElement);
                item.setTradeOperation(tradeOperation);
            } else if (listingPartElement.hasClass("market_listing_their_price")) {
                Integer price = getPriceWithoutFee(listingPartElement);
                item.setPrice(price);
            }
        }
    }

    protected void addFeeToPrice(TradeItem item) {
        Integer listingPriceUsdCents = item.getPrice();
        Integer priceWithFee = FinanceUtils.addFee(listingPriceUsdCents);
        item.setPrice(priceWithFee);
    }

    protected Integer getPriceWithoutFee(Element listingPartElement) {
        Elements priceElement = listingPartElement.getElementsByClass("market_listing_price");
        String rawPriceValue = Optional.ofNullable(priceElement.first())
                .map(Element::ownText)
                .orElse("")
                .replace("$", "")
                .replace(" USD", "");
        if (rawPriceValue.isBlank()) {
            return null;
        }
        return FinanceUtils.toCents(rawPriceValue);
    }

    protected String getItemName(Element listingPartElement) {
        Elements nameTag = listingPartElement.getElementsByTag("span");
        return Optional.ofNullable(nameTag.first())
                .map(Element::ownText)
                .orElse(null);
    }

    protected TradeItem.TradeOperation getTradeOperation(Element listingPartElement) {
        return switch (listingPartElement.ownText()) {
            case "-" -> TradeItem.TradeOperation.SOLD;
            case "+" -> TradeItem.TradeOperation.BOUGHT;
            default -> TradeItem.TradeOperation.OTHER;
        };
    }

}
