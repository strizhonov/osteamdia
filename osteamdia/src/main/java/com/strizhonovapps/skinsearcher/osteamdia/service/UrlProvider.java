package com.strizhonovapps.skinsearcher.osteamdia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UrlProvider {

    @Value("${url.steam-trade-history}")
    private final String steamTradeHistoryUrl;
    @Value("${url.steam-price-history}")
    private final String steamPriceHistoryUrl;
    @Value("${url.enhance-csgofloat}")
    private final String csgofloatEnchanceUrl;

    public URI getUrlForSteamTradeHistory(int start, int count) {
        return UriComponentsBuilder.fromHttpUrl(steamTradeHistoryUrl)
                .queryParam("start", start)
                .queryParam("count", count)
                .build()
                .toUri();
    }

    public URI getUrlForHistoryPrice(String itemName) {
        Objects.requireNonNull(itemName);
        return UriComponentsBuilder.fromHttpUrl(steamPriceHistoryUrl)
                .path(itemName)
                .build()
                .toUri();
    }

    public URI getUrlForItemDataFromCsgofloat(String steamLink) {
        Objects.requireNonNull(steamLink);
        return UriComponentsBuilder.fromHttpUrl(csgofloatEnchanceUrl)
                .queryParam("url", steamLink)
                .build()
                .toUri();
    }

}
