package com.strizhonovapps.skinsearcher.osteamdia.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsGoFloatRestTemplate implements RestTemplateDecorator {

    @Value("${app.steam.retryNum:3}")
    private final Integer retryNum;
    private final GenericRestTemplate genericRestTemplate;

    @Override
    public String sendGetRequest(URI uri, HttpHeaders headers) {
        return doSendGetRequest(uri, headers, 0);
    }

    private String doSendGetRequest(URI uri, HttpHeaders headers, int attempt) {
        try {
            return genericRestTemplate.sendGetRequest(uri, headers);
        } catch (Exception e) {
            if (attempt < retryNum) {
                return doSendGetRequest(uri, headers, attempt + 1);
            } else {
                throw e;
            }
        }
    }

}
