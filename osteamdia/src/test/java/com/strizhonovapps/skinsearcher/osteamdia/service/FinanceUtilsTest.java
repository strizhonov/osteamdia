package com.strizhonovapps.skinsearcher.osteamdia.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FinanceUtilsTest {

    @Test
    void shouldThrowRuntimeExceptionForIllegalStringDouble() {
        assertThrows(RuntimeException.class, () -> FinanceUtils.toCents("1212.12121.1212"));
        assertThrows(RuntimeException.class, () -> FinanceUtils.toCents("1212.12121.1212"));
        assertThrows(RuntimeException.class, () -> FinanceUtils.toCents("NON_DIGIT"));
        assertThrows(RuntimeException.class, () -> FinanceUtils.toCents("#$%^$$#"));
    }

    @Test
    void shouldConvertStringUsdValueToCents() {
        assertEquals(4200, FinanceUtils.toCents("42"));
        assertEquals(55, FinanceUtils.toCents("0.55"));
        assertEquals(-100, FinanceUtils.toCents("-1"));
    }

    @Test
    void shouldAddFeeToPrice() {
        assertEquals(4, FinanceUtils.addFee(2));
        assertEquals(115, FinanceUtils.addFee(100));
        assertEquals(5520, FinanceUtils.addFee(4800));
    }
}

