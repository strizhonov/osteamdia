package com.strizhonovapps.skinsearcher.osteamdia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class AppliedStickerState {

    @Id
    @SequenceGenerator(name = "APPLIED_STICKER_STATE_GEN", sequenceName = "APPLIED_STICKER_STATE_SQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPLIED_STICKER_STATE_GEN")
    private Long id;
    private Integer wear;
    private Integer priceOnTradeDate;
    @ManyToOne
    private StickerEntity stickerEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppliedStickerState that = (AppliedStickerState) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
