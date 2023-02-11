package com.strizhonovapps.skinsearcher.osteamdia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {HoverParser.class})
@ExtendWith(SpringExtension.class)
class HoverParserTest {

    @Autowired
    private HoverParser hoverParser;

    @Test
    void shouldReturnEmptyResultOnSourceWithNoHistoryRows() {
        assertTrue(hoverParser.parseHovers("Hovers").isEmpty());
        assertTrue(hoverParser.parseHovers("\r\n").isEmpty());
        assertTrue(hoverParser.parseHovers("").isEmpty());
        assertTrue(hoverParser.parseHovers("\r\nHovers").isEmpty());
    }

    @Test
    void shouldParseSingleHover() {
        String testHover = "\tCreateItemHoverFromContainer( g_rgAssets, 'history_row_4241343943026425742_4241863943021125743_name', 730, '2', '21613513640', 0 );\n";
        Map<String, String> hovers = hoverParser.parseHovers(testHover);
        assertEquals(1, hovers.size());
        assertTrue(hovers.containsKey("history_row_4241343943026425742_4241863943021125743"));
        assertEquals("21613513640", hovers.get("history_row_4241343943026425742_4241863943021125743"));
    }
}

