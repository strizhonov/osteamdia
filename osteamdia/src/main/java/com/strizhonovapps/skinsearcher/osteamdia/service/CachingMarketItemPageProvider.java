package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.strizhonovapps.skinsearcher.osteamdia.request.SteamRestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class CachingMarketItemPageProvider {

    private final SteamRestTemplate restTemplateManager;

    @Cacheable("marketItemPage")
    public String getMarketItemHtmlPage(URI marketItemUri) {
        return restTemplateManager.sendGetRequest(marketItemUri);
    }
}
