package org.piotrowski.cardureadoo.web.dto.user;

import java.util.Set;

public record LoginResponse(
        String username,
        Set<String> roles,
        String token
) { }
