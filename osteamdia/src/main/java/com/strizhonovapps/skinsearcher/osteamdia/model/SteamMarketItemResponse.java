package com.strizhonovapps.skinsearcher.osteamdia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamMarketItemResponse {

    private String name;
    private Integer volume;
    private Integer usdMedianPriceCents;
    private Integer usdLowestPriceCents;
}
