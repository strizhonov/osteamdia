package com.strizhonovapps.skinsearcher.osteamdia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeItem {

    private String name;
    private TradeOperation tradeOperation;
    private Integer price;
    private String historyCode;
    @Nullable
    private LocalDate dateCompleted;
    @Nullable
    private LocalDate dateCreated;

    public enum TradeOperation {
        SOLD,
        BOUGHT,
        OTHER,
    }
}
