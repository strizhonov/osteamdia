package com.strizhonovapps.skinsearcher.osteamdia.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Builder
public class CsGoFloatAssetResponse {

    private final Double floatValue;
    @Builder.Default
    private final List<AppliedStickerDto> appliedStickers = new ArrayList<>();

}
