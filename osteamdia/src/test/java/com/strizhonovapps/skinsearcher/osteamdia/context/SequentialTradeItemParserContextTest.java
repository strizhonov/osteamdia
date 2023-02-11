package com.strizhonovapps.skinsearcher.osteamdia.context;

import com.strizhonovapps.skinsearcher.osteamdia.service.SequentialTradeItemsParser;
import com.strizhonovapps.skinsearcher.osteamdia.service.TradeItemsParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"app.secure.steam=any", "app.sequentialHistory=true"})
class SequentialTradeItemParserContextTest {

    @Test
    void defaultTradeItemsParserLoads(@Autowired TradeItemsParser parser) {
        assertEquals(SequentialTradeItemsParser.class, parser.getClass());
    }
}