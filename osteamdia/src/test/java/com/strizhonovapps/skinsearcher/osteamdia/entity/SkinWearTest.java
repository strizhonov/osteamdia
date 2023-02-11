package com.strizhonovapps.skinsearcher.osteamdia.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SkinWearTest {

    @Test
    void shouldFindSkinWearInstanceFromStringToken() {
        assertThrows(IllegalStateException.class, () -> SkinWear.from("jane.doe@example.org"));
        assertEquals(SkinWear.BATTLE_SCARRED, SkinWear.from("Battle-Scarred"));
        assertEquals(SkinWear.FIELD_TESTED, SkinWear.from("Field-Tested"));
        assertEquals(SkinWear.WELL_WORN, SkinWear.from("Well-Worn"));
    }

    @Test
    void shouldThrowRuntimeExceptionOnIllegalStringToken() {
        assertThrows(RuntimeException.class, () -> SkinWear.from("ILLEGAL_TOKEN"));
    }
}

