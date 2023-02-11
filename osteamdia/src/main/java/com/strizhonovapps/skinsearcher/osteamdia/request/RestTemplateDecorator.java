package com.strizhonovapps.skinsearcher.osteamdia.request;


import org.springframework.http.HttpHeaders;

import java.net.URI;

public interface RestTemplateDecorator {

    String sendGetRequest(URI uri, HttpHeaders headers);

    default String sendGetRequest(URI uri) {
        return sendGetRequest(uri, new HttpHeaders());
    }

}
