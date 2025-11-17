package org.piotrowski.cardureadoo.application.port.in;

import org.piotrowski.cardureadoo.application.service.ExpansionApplicationService;
import org.piotrowski.cardureadoo.domain.model.Expansion;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ExpansionService {
    @Transactional
    Expansion upsert(ExpansionApplicationService.UpsertExpansionCommand cmd);

    @Transactional(readOnly = true)
    Optional<Expansion> findByExternalId(String externalId);

    @Transactional(readOnly = true)
    boolean exists(String externalId);

    @Transactional
    int deleteById(Long id);

    @Transactional
    int deleteByExternalId(String externalId);

    @Transactional
    int deleteByName(String name);

    @Transactional
    void patch(String externalId, PatchExpansionCommand cmd);

    record UpsertExpansionCommand(String externalId, String name) {}
    record PatchExpansionCommand(String name) {}
}
