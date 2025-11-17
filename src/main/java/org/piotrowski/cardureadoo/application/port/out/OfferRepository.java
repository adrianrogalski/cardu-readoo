package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.offer.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OfferRepository {
    void save(Offer offer);
    List<Offer> find(ExpansionExternalId expId, CardNumber number, Instant from, Instant to);
    Optional<Offer> findLast(ExpansionExternalId expId, CardNumber number);
    void deleteById(Long id);
    void deleteByCardId(Long cardId);
    void deleteByCardIds(List<Long> cardIds);
    void patch(long offerId, Money price, Instant listedAt);

    OfferStats stats(ExpansionExternalId expId, CardNumber number, Instant from, Instant to);

    record OfferStats(BigDecimal min, BigDecimal max, BigDecimal avg, long count) {}
}
