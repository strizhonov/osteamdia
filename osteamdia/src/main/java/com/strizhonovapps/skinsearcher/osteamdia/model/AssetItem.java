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
public class AssetItem {

    private String id;
    private String name;
    private String steamLink;
    private String nameTag;
    private Integer commodity;
    private List<String> appliedStickers = new ArrayList<>();

}
