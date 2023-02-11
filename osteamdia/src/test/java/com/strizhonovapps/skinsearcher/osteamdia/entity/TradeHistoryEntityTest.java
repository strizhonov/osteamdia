package com.strizhonovapps.skinsearcher.osteamdia.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TradeHistoryEntityTest {

    @Test
    void shouldReturnSameEntityOnAddStickers() {
        TradeHistoryEntity tradeHistoryEntity = new TradeHistoryEntity();
        assertSame(tradeHistoryEntity, tradeHistoryEntity.addStickers(new ArrayList<>()));
    }

    @Test
    void shouldAddStickers() {
        TradeHistoryEntity tradeHistoryEntity = new TradeHistoryEntity();
        List<AppliedStickerState> appliedStickers = List.of(
                new AppliedStickerState(),
                new AppliedStickerState(),
                new AppliedStickerState()
        );
        TradeHistoryEntity historyItem = tradeHistoryEntity.addStickers(appliedStickers);
        assertSame(tradeHistoryEntity, historyItem);
        assertEquals(3, historyItem.getStickers().size());
    }

    @Test
    void shouldThrowRuntimeExceptionOnOverflowOfStickers() {
        TradeHistoryEntity tradeHistoryEntity = new TradeHistoryEntity();
        List<AppliedStickerState> appliedStickers = List.of(
                new AppliedStickerState(),
                new AppliedStickerState(),
                new AppliedStickerState(),
                new AppliedStickerState(),
                new AppliedStickerState(),
                new AppliedStickerState()
        );
        assertThrows(RuntimeException.class, () -> tradeHistoryEntity.addStickers(appliedStickers));
    }
}

