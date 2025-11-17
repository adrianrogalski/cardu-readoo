package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;

import java.util.List;
import java.util.Optional;

public interface CardRepository {
    Optional<Card> find(ExpansionExternalId expId, CardNumber number);
    boolean exists(ExpansionExternalId expId, CardNumber number);
    Card save(Card card);
    List<Card> listAll(int page, int size);
    List<Card> listByExpansion(ExpansionExternalId expId, int page, int size);
    List<Card> searchByName(String query, int page, int size);
    void deleteById(Long id);
    Optional<Long> findIdByExpansionAndNumber(String expExternalId, String cardNumber);
    List<Long> findIdsByExpansionAndName(String expExternalId, String cardName);
    int deleteByIds(List<Long> ids);
    List<Long> findIdsByExpansion(String expExternalId);
    void patch(ExpansionExternalId expId, CardNumber cardNumber, CardName cardName, CardRarity cardRarity);
}
