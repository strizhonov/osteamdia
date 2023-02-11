package com.strizhonovapps.skinsearcher.osteamdia.repository;


import com.strizhonovapps.skinsearcher.osteamdia.entity.AppliedStickerState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppliedStickerStateRepository extends JpaRepository<AppliedStickerState, Long> {

    @Query("select s from AppliedStickerState s " +
            "where :name = s.stickerEntity.name " +
            "and :wear = s.wear " +
            "and :price = s.priceOnTradeDate")
    Optional<AppliedStickerState> find(String name, Integer wear, Integer price);

}
