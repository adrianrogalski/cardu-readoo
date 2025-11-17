package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "CARD_OFFER",
        indexes = {
                @Index(name = "IDX_OFFER_CARD_TIME", columnList = "CARD_ID, LISTED_AT")
        }
)
public class OfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CARD_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_OFFER_CARD")
    )
    private CardEntity card;

    @Column(name = "PRICE_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal priceAmount;

    @Column(name = "PRICE_CURRENCY", nullable = false, length = 3)
    private String priceCurrency;

    @Column(name = "LISTED_AT", nullable = false, updatable = false)
    private Instant listedAt;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OfferEntity(CardEntity card, BigDecimal priceAmount, String priceCurrency, Instant listedAt) {
        this.card = card;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
        this.listedAt = listedAt;
    }

    public void changePrice(BigDecimal newAmount, String newCurrency) {
        this.priceAmount = newAmount;
        this.priceCurrency = newCurrency;
    }

    public void rescheduleTo(Instant when) { this.listedAt = when; }


    public void referTo(CardEntity card) { this.card = card; }
}
