package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strizhonovapps.skinsearcher.osteamdia.model.CsGoFloatAssetResponse;
import com.strizhonovapps.skinsearcher.osteamdia.request.CsGoFloatRestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsGoFloatRetriever {

    private final UrlProvider urlProvider;
    private final CsGoFloatRestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public CsGoFloatAssetResponse getFloatResponse(String steamLink) {
        URI uri = urlProvider.getUrlForItemDataFromCsgofloat(steamLink);
        String csGoFloatAssetResponse = restTemplate.sendGetRequest(uri);
        return objectMapper.readerFor(CsGoFloatAssetResponse.class).readValue(csGoFloatAssetResponse);
    }
}
