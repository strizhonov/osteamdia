package com.strizhonovapps.skinsearcher.osteamdia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SteamHistoryResponseWrapper {

    private Integer page;
    private Integer totalCount;
    private String hovers;
    private String resultHtml;
    private List<AssetItem> assetItems = new ArrayList<>();

}
