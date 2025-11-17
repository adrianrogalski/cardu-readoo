package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.adapters;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.out.OfferRepository;
import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.offer.Money;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper.OfferMapper;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.CardJpaRepository;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.OfferJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OfferJpaRepositoryAdapter implements OfferRepository {

    private final OfferJpaRepository offerJpa;
    private final CardJpaRepository cardJpa;
    private final OfferMapper mapper;

    @Override
    @Transactional
    public void save(Offer offer) {
        var card = cardJpa.findByExpansionExternalIdAndCardNumber(
                    offer.getExpansionId().value(),
                    offer.getCardNumber().value())
                .orElseThrow(() -> new IllegalStateException("Card not found for offer"));

        var entity = mapper.toEntity(offer, card);
        entity.referTo(card);
        offerJpa.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> find(ExpansionExternalId expId, CardNumber number, Instant from, Instant to) {
        var cardId = cardJpa.findByExpansionExternalIdAndCardNumber(expId.value(), number.value())
                .map(CardEntity::getId)
                .orElseThrow(() -> new IllegalStateException("Card not found"));

        return offerJpa
                .findByCardIdAndListedAtBetweenOrderByListedAtAsc(cardId, from, to)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offer> findLast(ExpansionExternalId expId, CardNumber number) {
        return cardJpa.findByExpansionExternalIdAndCardNumber(expId.value(), number.value())
                .map(CardEntity::getId)
                .map(offerJpa::findTopByCardIdOrderByListedAtDesc)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public OfferStats stats(ExpansionExternalId expId, CardNumber number, Instant from, Instant to) {
        var cardId = cardJpa.findByExpansionExternalIdAndCardNumber(expId.value(), number.value())
                .map(CardEntity::getId)
                .orElseThrow(() -> new IllegalStateException("Card not found"));

        var p = offerJpa.statsForCardInRange(cardId, from, to);
        if (p == null || p.getCnt() == 0) {
            return new OfferStats(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0L);
        }
        return new OfferStats(p.getMin(), p.getMax(), p.getAvg(), p.getCnt());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        offerJpa.deleteByIdExplicit(id);
    }

    @Override
    public void deleteByCardId(Long cardId) {
        offerJpa.deleteByCardId(cardId);
    }

    @Override
    public void deleteByCardIds(List<Long> cardIds) {
        if (cardIds == null || cardIds.isEmpty()) return;
        offerJpa.deleteByCardIds(cardIds);
    }

    @Override
    @Transactional
    public void patch(long offerId, Money price, Instant listedAt) {
        var oe = offerJpa.findById(offerId)
                .orElseThrow(() -> new IllegalStateException("Offer not found: " + offerId));

        if (price != null && price.amount() != null) {
            var currency = price.currency() != null ? price.currency() : oe.getPriceCurrency();
            oe.changePrice(price.amount(), currency);
        } else if (price != null && price.currency() != null) {
            oe.changePrice(oe.getPriceAmount(), price.currency());
        }
        if (listedAt != null) {
            oe.rescheduleTo(listedAt);
        }
    }
}
