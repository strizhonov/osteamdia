package com.strizhonovapps.skinsearcher.osteamdia.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SKIN")
@Builder
public class SkinEntity {

    @Id
    @SequenceGenerator(name = "SKIN_GEN", sequenceName = "SKIN_SQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SKIN_GEN")
    private Long id;
    private String skinName;
    @Enumerated(EnumType.STRING)
    private SkinWear wear;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SkinEntity that = (SkinEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
