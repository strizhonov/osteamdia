package com.strizhonovapps.skinsearcher.osteamdia.repository;


import com.strizhonovapps.skinsearcher.osteamdia.entity.TradeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TradeHistoryRepository extends JpaRepository<TradeHistoryEntity, Long> {

    @Query("select count(e) > 0 from TradeHistoryEntity e " +
            "where (:#{#entity.skinEntity.skinName} = e.skinEntity.skinName)" +
            "and (:#{#entity.nameTag} = e.nameTag OR (e.nameTag is null and :#{#entity.nameTag} is null))" +
            "and (:#{#entity.skinFloat} = e.skinFloat)" +
            "and (:#{#entity.operation} = e.operation)" +
            "and (:#{#entity.dateCreated} = e.dateCreated)" +
            "and (:#{#entity.dateCompleted} = e.dateCompleted)" +
            "and (:#{#entity.sellPrice} = e.sellPrice)" +
            "and (:#{#entity.priceOnTradeDate} = e.priceOnTradeDate)")
    Boolean exists(TradeHistoryEntity entity);

}
