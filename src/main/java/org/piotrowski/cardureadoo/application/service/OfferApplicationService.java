package org.piotrowski.cardureadoo.application.service;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.in.OfferService;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.application.port.out.ExpansionRepository;
import org.piotrowski.cardureadoo.application.port.out.OfferRepository;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.Offer;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.offer.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OfferApplicationService implements OfferService {

    private final OfferRepository offerRepository;
    private final ExpansionRepository expansionRepository;
    private final CardRepository cardRepository;

    @Transactional
    @Override
    public void addOffer(AddOfferCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");

        final var expId = new ExpansionExternalId(cmd.expExternalId());
        final var cardNumber = new CardNumber(cmd.cardNumber());
        final var when = cmd.listedAt() != null ? cmd.listedAt() : Instant.now();
        final var currency = (cmd.currency() == null || cmd.currency().isBlank()) ? "PLN" : cmd.currency();
        final var price =  new Money(new BigDecimal(cmd.amount()), currency);
        final var rarity = new CardRarity(cmd.cardRarity());

        if (!cardRepository.exists(expId, cardNumber)) {
            final var name = cmd.cardName() != null ? new CardName(cmd.cardName()) : new CardName("UNKNOWN");
//            final var expansion = expansionRepository.findByExternalId(expId).orElseThrow((() -> new IllegalStateException("Expansion not found: " + expId)));
//            final var card = new Card(name, rarity, cardNumber, expId);
            final var card = Card.of(name, rarity, cardNumber, expId);
            cardRepository.save(card);
        }

        final var offer = Offer.of(expId, cardNumber, price, when);
        offerRepository.save(offer);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OfferPointDto> getOffers(String expExternalId, String cardNumber, Instant from, Instant to) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var num = new CardNumber(cardNumber);
        final var f = from != null ? from : Instant.EPOCH;
        final var t = to != null ? to : Instant.now();

        return offerRepository.find(expId, num, f, t)
                .stream()
                .map(o -> new OfferPointDto(o.getListedAt(), o.getPrice().amount(), o.getPrice().currency()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public OfferPointDto getLast(String expExternalId, String cardNumber) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var num = new CardNumber(cardNumber);

        return offerRepository.findLast(expId, num)
                .map(o -> new OfferPointDto(o.getListedAt(), o.getPrice().amount(), o.getPrice().currency()))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public OfferStatsDto getStats(String expExternalId, String cardNumber, Instant from, Instant to) {
        final var expId = new ExpansionExternalId(expExternalId);
        final var num = new CardNumber(cardNumber);
        final var f = from != null ? from : Instant.EPOCH;
        final var t = to != null ? to : Instant.now();

        final var s = offerRepository.stats(expId, num, f, t);
        return new OfferStatsDto(s.min(), s.max(), s.avg(), s.count());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        offerRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void patch(long offerId, PatchOfferCommand cmd) {
        var money = (cmd != null && (cmd.amount() != null || cmd.currency() != null))
                ? Money.of(cmd.amount() != null ? cmd.amount() : null, cmd.currency()) : null;

        var listedAt = (cmd != null) ? cmd.listedAt() : null;
        offerRepository.patch(offerId, money, listedAt);
    }
}
