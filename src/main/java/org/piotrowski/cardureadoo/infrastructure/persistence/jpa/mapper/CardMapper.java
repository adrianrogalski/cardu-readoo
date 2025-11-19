package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.piotrowski.cardureadoo.domain.model.Card;
import org.piotrowski.cardureadoo.domain.model.value.card.CardName;
import org.piotrowski.cardureadoo.domain.model.value.card.CardNumber;
import org.piotrowski.cardureadoo.domain.model.value.card.CardRarity;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.CardEntity;

@Mapper(componentModel = "spring", imports = { CardName.class, CardNumber.class, CardRarity.class, ExpansionExternalId.class })
public interface CardMapper {

    @Mapping(target = "name",        expression = "java(src.getName().value())")
    @Mapping(target = "cardNumber",  expression = "java(src.getNumber().value())")
    @Mapping(target = "cardRarity",  expression = "java(src.getRarityCard().value())")
    @Mapping(target = "expansion",   ignore = true)
    CardEntity toEntity(Card src);

    default Card toDomain(CardEntity e) {
        return new Card(
                new CardName(e.getName()),
                new CardRarity(e.getCardRarity()),
                new CardNumber(e.getCardNumber()),
                new ExpansionExternalId(e.getExpansion().getExternalId())
        );
    }
}
