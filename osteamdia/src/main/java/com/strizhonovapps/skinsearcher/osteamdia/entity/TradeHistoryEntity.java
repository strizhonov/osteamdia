package com.strizhonovapps.skinsearcher.osteamdia.entity;

import com.strizhonovapps.skinsearcher.osteamdia.model.TradeItem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class TradeHistoryEntity {

    @Id
    @SequenceGenerator(name = "TRADE_HISTORY_ENTITY_GEN", sequenceName = "TRADE_HISTORY_ENTITY_SQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRADE_HISTORY_ENTITY_GEN")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "SKIN_ENTITY_ID")
    private SkinEntity skinEntity;
    private String nameTag;
    private Double skinFloat;
    @Enumerated(EnumType.STRING)
    private TradeItem.TradeOperation operation;
    private LocalDate dateCreated;
    private LocalDate dateCompleted;
    private Integer sellPrice;
    private Integer priceOnTradeDate;
    @ManyToOne
    @JoinColumn(name = "FIRST_STICKER_ID")
    private AppliedStickerState firstSticker;
    @ManyToOne
    @JoinColumn(name = "SECOND_STICKER_ID")
    private AppliedStickerState secondSticker;
    @ManyToOne
    @JoinColumn(name = "THIRD_STICKER_ID")
    private AppliedStickerState thirdSticker;
    @ManyToOne
    @JoinColumn(name = "FOURTH_STICKER_ID")
    private AppliedStickerState fourthSticker;
    @ManyToOne
    @JoinColumn(name = "FIFTH_STICKER_ID")
    private AppliedStickerState fifthSticker;

    public TradeHistoryEntity addStickers(Collection<AppliedStickerState> stickers) {
        stickers.forEach(this::addSticker);
        return this;
    }

    public List<AppliedStickerState> getStickers() {
        return Stream.of(firstSticker, secondSticker, thirdSticker, fourthSticker, fifthSticker)
                .filter(Objects::nonNull)
                .toList();
    }

    private void addSticker(AppliedStickerState stickerState) {
        if (firstSticker == null) {
            setFirstSticker(stickerState);
        } else if (secondSticker == null) {
            setSecondSticker(stickerState);
        } else if (thirdSticker == null) {
            setThirdSticker(stickerState);
        } else if (fourthSticker == null) {
            setFourthSticker(stickerState);
        } else if (fifthSticker == null) {
            setFifthSticker(stickerState);
        } else {
            throw new IllegalStateException("Unable to add sticker to trade entity " + this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TradeHistoryEntity that = (TradeHistoryEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
