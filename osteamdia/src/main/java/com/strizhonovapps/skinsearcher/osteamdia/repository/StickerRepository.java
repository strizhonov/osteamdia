package com.strizhonovapps.skinsearcher.osteamdia.repository;


import com.strizhonovapps.skinsearcher.osteamdia.entity.StickerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StickerRepository extends JpaRepository<StickerEntity, Long> {

    Optional<StickerEntity> findByName(String name);
}
