package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionName;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.ExpansionEntity;

@Mapper(componentModel = "spring", imports = { ExpansionExternalId.class, ExpansionName.class })
public interface ExpansionMapper {

    @Mapping(target = "externalId", expression = "java(src.getId().value())")
    @Mapping(target = "name",       expression = "java(src.getName().value())")
    ExpansionEntity toEntity(Expansion src);

    default Expansion toDomain(ExpansionEntity e) {
        return new Expansion(
                new ExpansionExternalId(e.getExternalId()),
                new ExpansionName(e.getName())
        );
    }
}
