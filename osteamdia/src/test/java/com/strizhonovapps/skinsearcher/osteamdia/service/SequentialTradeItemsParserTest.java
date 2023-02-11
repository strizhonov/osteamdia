package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.strizhonovapps.skinsearcher.osteamdia.model.TradeItem;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequentialTradeItemsParserTest {

    private static final String TEST_HISTORY_HTML = "history_results.html";

    private final SequentialTradeItemsParser parser = new SequentialTradeItemsParser(1999);

    @Test
    void shouldParseValidItems() throws IOException {
        String content = readTestResource(TEST_HISTORY_HTML);
        List<TradeItem> result = parser.parse(content);
        assertEquals(2, result.size());
        TradeItem firstActual = result.get(0);
        TradeItem firstExpected = TradeItem.builder()
                .name("Revolution Case")
                .tradeOperation(TradeItem.TradeOperation.SOLD)
                .price(288)
                .historyCode("history_row_4241863243026425742_4241863943026425743")
                .dateCompleted(LocalDate.of(1999, 2, 11))
                .dateCreated(LocalDate.of(1999, 2, 11))
                .build();
        assertEquals(firstExpected, firstActual);

        TradeItem secondActual = result.get(1);
        TradeItem secondExpected = TradeItem.builder()
                .name("Revolution Case")
                .tradeOperation(TradeItem.TradeOperation.OTHER)
                .price(250)
                .historyCode("history_row_4241863243026425742_event_1")
                .dateCompleted(null)
                .dateCreated(LocalDate.of(1999, 2, 11))
                .build();
        assertEquals(secondExpected, secondActual);
    }

    @SuppressWarnings("all")
    private String readTestResource(String resourcePath) throws IOException {
        String file = getClass().getClassLoader().getResource(resourcePath).getFile();
        return Files.readString(new File(file).toPath());
    }
}

