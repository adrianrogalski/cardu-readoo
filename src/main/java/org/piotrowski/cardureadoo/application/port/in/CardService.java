package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.application.service.CardApplicationService;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CardService {
    @Transactional
    Card save(UpsertCardCommand cmd);

    @Transactional(readOnly = true)
    Optional<Card> find(String expExternalId, String cardNumber);

    @Transactional(readOnly = true)
    boolean exists(String expExternalId, String cardNumber);

    @Transactional(readOnly = true)
    List<Card> listAll(int page, int size);

    @Transactional(readOnly = true)
    List<Card> listByExpansion(String expExternalId, int page, int size);

    @Transactional(readOnly = true)
    List<Card> searchByName(String query, int page, int size);

    @Transactional
    void deleteById(Long id);

    @Transactional
    int deleteByExpansionAndNumber(String expExternalId, String cardNumber);

    @Transactional
    int deleteByExpansionAndName(String expExternalId, String cardName);

    @Transactional
    void patch(String expExternalId, String cardNumber, PatchCardCommand cmd);

    record UpsertCardCommand(String expExternalId, String cardNumber, String cardName, String cardRarity) {}
    record PatchCardCommand(String cardName, String cardRarity) {}
}

