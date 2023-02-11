package com.strizhonovapps.skinsearcher.osteamdia.service;

import com.strizhonovapps.skinsearcher.osteamdia.request.SteamRestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {HistoryPriceProvider.class})
@ExtendWith(SpringExtension.class)
class HistoryPriceProviderTest {

    @Autowired
    private HistoryPriceProvider historyPriceProvider;
    @MockBean
    private SteamRestTemplate steamRestTemplate;
    @MockBean
    private UrlProvider urlProvider;

    @Test
    void shouldThrowRuntimeExceptionWhenResponseDoesntContainNeededDate() {
        String skinName = "AK-47 Red Line";
        URI testUri = URI.create("testUri");
        when(urlProvider.getUrlForHistoryPrice(skinName)).thenReturn(testUri);
        when(steamRestTemplate.sendGetRequest(testUri)).thenReturn("RESPONSE_WITHOUT_DATA");
        LocalDate now = LocalDate.now();
        assertThrows(RuntimeException.class, () -> historyPriceProvider.findPrice(skinName, now));
    }

    @Test
    void shouldCalculateAverageForSingleDate() {
        String skinName = "AK-47 Red Line";
        URI testUri = URI.create("testUri");
        when(urlProvider.getUrlForHistoryPrice(skinName)).thenReturn(testUri);
        when(steamRestTemplate.sendGetRequest(testUri)).thenReturn("ANY_HTML_CONTENT" +
                "var line1 = " +
                "[[\"Feb 18 2016 01: +0\", 6.738, \"203\"], " +
                "[\"Jan 31 2017 01: +0\", 2.205, \"105\"], " +
                "[\"Feb 06 2017 01: +0\", 2.206, \"81\"], " +
                "[\"Feb 07 2017 01: +0\", 2.223, \"69\"], " +
                "[\"Mar 08 2017 01: +0\", 2.21, \"75\"], " +
                "[\"Apr 09 2017 01: +0\", 2.152, \"83\"], " +
                "[\"Feb 21 2023 23: +0\", 3.16, \"4\"], " +
                "[\"Feb 22 2023 04: +0\", 3.075, \"1\"], " +
                "[\"Feb 22 2023 16: +0\", 3.341, \"5\"]];\n" +
                "ANY_HTML_CONTENT"
        );
        LocalDate sellDate = LocalDate.of(2017, 2, 7);
        assertEquals(222, historyPriceProvider.findPrice(skinName, sellDate).orElseThrow());
    }

    @Test
    void shouldCalculateAverageForMultipleDates() {
        String skinName = "AK-47 Red Line";
        URI testUri = URI.create("testUri");
        when(urlProvider.getUrlForHistoryPrice(skinName)).thenReturn(testUri);
        when(steamRestTemplate.sendGetRequest(testUri)).thenReturn("ANY_HTML_CONTENT" +
                "var line1 = " +
                "[[\"Feb 18 2016 01: +0\", 6.738, \"203\"], " +
                "[\"Feb 03 2017 01: +0\", 2.177, \"96\"], " +
                "[\"Feb 05 2017 01: +0\", 2.133, \"91\"], " +
                "[\"Mar 08 2017 01: +0\", 2.21, \"75\"], " +
                "[\"Apr 09 2017 01: +0\", 2.152, \"83\"], " +
                "[\"Feb 21 2023 23: +0\", 3.16, \"4\"], " +
                "[\"Feb 22 2023 04: +0\", 3.075, \"1\"], " +
                "[\"Feb 22 2023 05: +0\", 3.075, \"1\"], " +
                "[\"Feb 22 2023 10: +0\", 2.16, \"2\"], " +
                "[\"Feb 22 2023 16: +0\", 3.341, \"5\"]];\n" +
                "ANY_HTML_CONTENT"
        );
        LocalDate sellDate = LocalDate.of(2023, 2, 22);
        assertEquals(291, historyPriceProvider.findPrice(skinName, sellDate).orElseThrow());
    }

    @Test
    void shouldPickPreviousDateIfCurrentIsAbsent() {
        String skinName = "AK-47 Red Line";
        URI testUri = URI.create("testUri");
        when(urlProvider.getUrlForHistoryPrice(skinName)).thenReturn(testUri);
        when(steamRestTemplate.sendGetRequest(testUri)).thenReturn("ANY_HTML_CONTENT" +
                "var line1 = " +
                "[[\"Feb 18 2016 01: +0\", 6.738, \"203\"], " +
                "[\"Jan 31 2017 01: +0\", 2.205, \"105\"], " +
                "[\"Feb 06 2017 01: +0\", 2.206, \"81\"], " +
                "[\"Feb 07 2017 01: +0\", 2.223, \"69\"], " +
                "[\"Mar 08 2017 01: +0\", 2.21, \"75\"], " +
                "[\"Apr 09 2017 01: +0\", 2.152, \"83\"], " +
                "[\"Feb 21 2023 23: +0\", 3.16, \"4\"], " +
                "[\"Feb 22 2023 04: +0\", 3.075, \"1\"]];\n" +
                "ANY_HTML_CONTENT"
        );
        LocalDate sellDate = LocalDate.of(2020, 2, 20);
        assertEquals(215, historyPriceProvider.findPrice(skinName, sellDate).orElseThrow());
    }
}

