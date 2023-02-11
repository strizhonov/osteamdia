package com.strizhonovapps.skinsearcher.osteamdia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkinListingDto {

    private String name;
    private Integer listingUsdPriceCents;
    private String steamLink;
    private String nameTag;
    private List<String> appliedStickers = new ArrayList<>();

}
