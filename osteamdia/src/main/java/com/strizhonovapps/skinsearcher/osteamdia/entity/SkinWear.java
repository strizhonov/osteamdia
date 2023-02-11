package com.strizhonovapps.skinsearcher.osteamdia.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SkinWear {

    FACTORY_NEW("Factory New"),
    MINIMAL_WEAR("Minimal Wear"),
    FIELD_TESTED("Field-Tested"),
    WELL_WORN("Well-Worn"),
    BATTLE_SCARRED("Battle-Scarred");

    private final String wearToken;

    public static SkinWear from(String nameToFindFrom) {
        return Arrays.stream(SkinWear.values())
                .filter(item -> nameToFindFrom.contains(item.wearToken))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Provided skin name {" + nameToFindFrom + "} is not a valid skin name"));
    }
}
