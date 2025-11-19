package org.piotrowski.cardureadoo.web.dto.card;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.piotrowski.cardureadoo.domain.model.Card;

@Mapper(componentModel = "spring")
public interface CardDtoMapper {

    @Mapping(target = "expExternalId", expression = "java(card.getExpansionId().value())")
    @Mapping(target = "cardNumber",    expression = "java(card.getNumber().value())")
    @Mapping(target = "cardName",      expression = "java(card.getName().value())")
    @Mapping(target = "cardRarity",    expression = "java(card.getRarityCard().value())")
    CardResponse toResponse(Card card);

}
