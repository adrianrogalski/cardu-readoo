package org.piotrowski.cardureadoo.web.dto.user;

import java.util.Set;

public record CreateUserResponse(
        Long id,
        String username,
        Set<String> roles
) { }
