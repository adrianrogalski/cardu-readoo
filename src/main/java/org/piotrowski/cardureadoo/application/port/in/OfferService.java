package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.application.service.OfferApplicationService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface OfferService {

    @Transactional
    void addOffer(AddOfferCommand cmd);

    @Transactional(readOnly = true)
    List<OfferPointDto> getOffers(String expExternalId, String cardNumber, Instant from, Instant to);

    @Transactional(readOnly = true)
    OfferPointDto getLast(String expExternalId, String cardNumber);

    @Transactional(readOnly = true)
    OfferStatsDto getStats(String expExternalId, String cardNumber, Instant from, Instant to);

    @Transactional
    void deleteById(Long id);

    @Transactional
    void patch(long offerId, PatchOfferCommand cmd);

    record AddOfferCommand(
            String expExternalId,
            String cardNumber,
            String amount,
            String currency,
            Instant listedAt,
//            Long userId
            String cardName,
            String cardRarity
    ) {}
    record PatchOfferCommand(BigDecimal amount, String currency, Instant listedAt) {}
    record OfferPointDto(Instant listedAt, BigDecimal amount, String currency) {}
    record OfferStatsDto(BigDecimal min, BigDecimal  max, BigDecimal avg, long count) {}
}
