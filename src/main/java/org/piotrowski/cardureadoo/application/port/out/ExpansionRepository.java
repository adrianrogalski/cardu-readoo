package org.piotrowski.cardureadoo.application.port.out;

import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionExternalId;
import org.piotrowski.cardureadoo.domain.model.value.expansion.ExpansionName;

import java.util.List;
import java.util.Optional;

public interface ExpansionRepository {
    Optional<Expansion> findByExternalId(ExpansionExternalId id);
    Optional<Expansion> findById(Long id);
    boolean existsByExternalId(ExpansionExternalId id);
    Expansion save(Expansion expansion);
    List<String> findExternalIdsByName(String name);
    int deleteById(Long id);
    int deleteByExternalId(String externalId);
    void patch(ExpansionExternalId id, ExpansionName name);
}
