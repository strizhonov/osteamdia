package com.strizhonovapps.skinsearcher.osteamdia.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.strizhonovapps.skinsearcher.osteamdia.model.CsGoFloatAssetResponse;
import com.strizhonovapps.skinsearcher.osteamdia.model.SteamHistoryResponseWrapper;
import com.strizhonovapps.skinsearcher.osteamdia.service.SequentialTradeItemsParser;
import com.strizhonovapps.skinsearcher.osteamdia.service.TradeItemsParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

@Configuration
public class CoreConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        StringHttpMessageConverter utf8Converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        restTemplate.getMessageConverters().add(0, utf8Converter);
        return restTemplate;
    }

    @Bean
    ObjectMapper objectMapper(JsonDeserializer<SteamHistoryResponseWrapper> steamHistoryResponseWrapperJsonDeserializer,
                              JsonDeserializer<CsGoFloatAssetResponse> csGoFloatAssetResponseJsonDeserializer) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SteamHistoryResponseWrapper.class, steamHistoryResponseWrapperJsonDeserializer);
        module.addDeserializer(CsGoFloatAssetResponse.class, csGoFloatAssetResponseJsonDeserializer);
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    @ConditionalOnProperty("app.sequentialHistory")
    Supplier<TradeItemsParser> sequentialTradeItemsParserSupplier(@Value("${app.latestTradeYear:#{null}}")
                                                                  Integer tradeItemsParserLatestTradeYear) {
        return () -> sequentialTradeItemsParser(tradeItemsParserLatestTradeYear);
    }

    @Bean
    @ConditionalOnProperty("app.sequentialHistory")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    TradeItemsParser sequentialTradeItemsParser(@Value("${app.latestTradeYear:#{null}}")
                                                Integer tradeItemsParserLatestTradeYear) {
        return new SequentialTradeItemsParser(tradeItemsParserLatestTradeYear);
    }

    @Bean
    @ConditionalOnMissingBean(name = "sequentialTradeItemsParserSupplier")
    Supplier<TradeItemsParser> defaultTradeItemsParserSupplier() {
        return this::defaultTradeItemsParser;
    }

    @Bean
    @ConditionalOnMissingBean(TradeItemsParser.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    TradeItemsParser defaultTradeItemsParser() {
        return new TradeItemsParser();
    }
}