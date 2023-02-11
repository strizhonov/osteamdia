package com.strizhonovapps.skinsearcher.osteamdia.request;

import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class SteamRestTemplate implements RestTemplateDecorator {

    private static final double DEFAULT_LIMIT_RATE = 10 / 60d; // 10 per minute
    private static final double AFTER_TOO_MANY_REQUESTS_LIMIT_RATE = 1 / 60d / 5d; // 1 per 5 minutes

    @Value("${app.steam.retryNum:3}")
    private final Integer retryNum;
    private final GenericRestTemplate genericRestTemplate;
    private final RateLimiter rateLimiter = RateLimiter.create(DEFAULT_LIMIT_RATE);
    private final Lock rateLimiterLock = new ReentrantLock();
    private final BigDecimal defaultLimitRate = toBigDecimal(DEFAULT_LIMIT_RATE);
    private final BigDecimal shortenedLimitRate = toBigDecimal(AFTER_TOO_MANY_REQUESTS_LIMIT_RATE);

    @Override
    public String sendGetRequest(URI uri, HttpHeaders headers) {
        String successfulResponse = doSendGetRequest(uri, headers, 0);
        rateLimiterLock.lock();
        try {
            BigDecimal currentLimitRate = toBigDecimal(rateLimiter.getRate());
            if (currentLimitRate.compareTo(defaultLimitRate) != 0) {
                log.info("Setting the rate to the default value {}.", DEFAULT_LIMIT_RATE);
                rateLimiter.setRate(DEFAULT_LIMIT_RATE);
            }
        } finally {
            rateLimiterLock.unlock();
        }
        return successfulResponse;
    }

    @SneakyThrows
    private String doSendGetRequest(URI uri, HttpHeaders headers, int attempt) {
        try {
            rateLimiter.acquire();
            return genericRestTemplate.sendGetRequest(uri, headers);
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.debug("Too many requests for {}", uri.toString());
            rateLimiterLock.lock();
            try {
                BigDecimal currentLimitRate = toBigDecimal(rateLimiter.getRate());
                if (currentLimitRate.compareTo(shortenedLimitRate) != 0) {
                    log.info("Too many requests for Steam. Reducing the rate to {}.", AFTER_TOO_MANY_REQUESTS_LIMIT_RATE);
                    rateLimiter.setRate(AFTER_TOO_MANY_REQUESTS_LIMIT_RATE);
                }
            } finally {
                rateLimiterLock.unlock();
            }
            return doSendGetRequest(uri, headers, attempt);
        } catch (Exception e) {
            if (attempt < retryNum) {
                return doSendGetRequest(uri, headers, attempt + 1);
            } else {
                throw e;
            }
        }
    }

    private BigDecimal toBigDecimal(double defaultLimitRate) {
        return BigDecimal.valueOf(defaultLimitRate).setScale(5, RoundingMode.HALF_UP);
    }

}
