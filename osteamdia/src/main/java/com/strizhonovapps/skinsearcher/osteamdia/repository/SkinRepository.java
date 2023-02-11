package com.strizhonovapps.skinsearcher.osteamdia.repository;


import com.strizhonovapps.skinsearcher.osteamdia.entity.SkinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkinRepository extends JpaRepository<SkinEntity, Long> {

    Optional<SkinEntity> findBySkinName(String name);
}
