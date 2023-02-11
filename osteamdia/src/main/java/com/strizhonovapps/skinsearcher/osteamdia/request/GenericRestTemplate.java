package com.strizhonovapps.skinsearcher.osteamdia.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenericRestTemplate implements RestTemplateDecorator {

    private final RestTemplate restTemplate;

    @Override
    public String sendGetRequest(URI uri, HttpHeaders headers) {
        log.debug("Sending request, url: {}", uri);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                String.class
        ).getBody();
    }

}
