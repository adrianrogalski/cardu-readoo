package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.adapters;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.port.out.CardRepository;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper.CardMapper;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.CardJpaRepository;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.ExpansionJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CardJpaRepositoryAdapter implements CardRepository {

    private final CardJpaRepository cardJpa;
    private final ExpansionJpaRepository expansionJpa;
    private final CardMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Card> find(ExpansionExternalId expId, CardNumber number) {
        return cardJpa
                .findByExpansionExternalIdAndCardNumber(expId.value(), number.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(ExpansionExternalId expId, CardNumber number) {
        return cardJpa.existsByExpansionExternalIdAndCardNumber(expId.value(), number.value());
    }

    @Override
    @Transactional
    public Card save(Card card) {
        var exp = expansionJpa.findByExternalId(card.getExpansionId().value())
                .orElseThrow(() -> new IllegalStateException("Expansion not found: " + card.getExpansionId().value()));

        var existing = cardJpa.findByExpansionExternalIdAndCardNumber(card.getExpansionId().value(), card.getNumber().value());

        if (existing.isPresent()) {
            var e = existing.get();
            e.renameTo(card.getName().value());
            e.changeRarityTo(card.getRarityCard().value());
            return mapper.toDomain(cardJpa.save(e));
        }

        try {
            var entity  = mapper.toEntity(card);
            entity.attachTo(exp);
            return mapper.toDomain(cardJpa.save(entity));
        } catch (DataIntegrityViolationException dup) {
            return cardJpa.findByExpansionExternalIdAndCardNumber(
                            card.getExpansionId().value(), card.getNumber().value())
                    .map(mapper::toDomain)
                    .orElseThrow(() -> dup);
        }
    }

    @Override
    public List<Card> listAll(int page, int size) {
        Pageable p = PageRequest.of(safePage(page), safeSize(size), Sort.by("name").ascending());
        return cardJpa.findAll(p).map(mapper::toDomain).getContent();
    }

    @Override
    public List<Card> listByExpansion(ExpansionExternalId expId, int page, int size) {
        Pageable p = PageRequest.of(safePage(page), safeSize(size), Sort.by("cardNumber").ascending());
        return cardJpa
                .findByExpansionExternalId(expId.value(), p)
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public List<Card> searchByName(String query, int page, int size) {
        Pageable p = PageRequest.of(safePage(page), safeSize(size), Sort.by("name").ascending());
        return cardJpa.findByNameContainingIgnoreCase(query, p)
                .map(mapper::toDomain)
                .getContent();
    }

    @Override
    public void deleteById(Long id) {
        cardJpa.deleteById(id);
    }

    @Override
    public Optional<Long> findIdByExpansionAndNumber(String expExternalId, String cardNumber) {
        return cardJpa.findIdByExpansionExternalIdAndCardNumber(expExternalId, cardNumber);
    }

    @Override
    public List<Long> findIdsByExpansionAndName(String expExternalId, String cardName) {
        return cardJpa.findIdsByExpansionExternalIdAndName(expExternalId, cardName);
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return cardJpa.deleteByIds(ids);
    }

    @Override
    public List<Long> findIdsByExpansion(String expExternalId) {
        return cardJpa.findIdsByExpansionExternalId(expExternalId);
    }

    @Override
    @Transactional
    public void patch(ExpansionExternalId expId, CardNumber cardNumber, CardName cardName, CardRarity cardRarirty) {
        var ce = cardJpa.findByExpansionExternalIdAndCardNumber(expId.value(), cardNumber.value())
                .orElseThrow(() -> new IllegalStateException("Card not found: " + expId.value() + " / " + cardNumber.value()));

        if (cardName != null) {
            ce.renameTo(cardName.value());
        }

        if (cardRarirty != null) {
            ce.changeRarityTo(cardRarirty.value());
        }
    }

    private int safePage(int page) { return Math.max(0, page); }
    private int safeSize(int size) { return size <= 0 ? 50 : Math.min(size, 500); }
}
