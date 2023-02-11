package com.strizhonovapps.skinsearcher.osteamdia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeHistoryIntermediateDataChunk {

    private List<TradeItem> tradeItems = new ArrayList<>();
    private List<AssetItem> assetItems = new ArrayList<>();
    private String hovers;
}
