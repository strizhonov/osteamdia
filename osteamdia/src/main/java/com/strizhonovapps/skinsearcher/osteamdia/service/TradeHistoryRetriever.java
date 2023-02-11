package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strizhonovapps.skinsearcher.osteamdia.model.SteamHistoryResponseWrapper;
import com.strizhonovapps.skinsearcher.osteamdia.request.SteamRestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeHistoryRetriever {

    private final UrlProvider urlProvider;
    private final SteamRestTemplate steamRestTemplate;
    private final ObjectMapper mapper;

    @SneakyThrows
    public SteamHistoryResponseWrapper getHistoryResponse(String steamLoginSecure, int start, int count, int page) {
        String secureCookieValue = "steamLoginSecure=" + steamLoginSecure;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, secureCookieValue);
        URI uri = urlProvider.getUrlForSteamTradeHistory(start, count);
        String response = steamRestTemplate.sendGetRequest(uri, headers);
        SteamHistoryResponseWrapper result = mapper.readerFor(SteamHistoryResponseWrapper.class).readValue(response);
        result.setPage(page);
        return result;
    }

}