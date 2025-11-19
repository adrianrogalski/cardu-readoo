package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.piotrowski.cardureadoo.application.service.UserApplicationService;
import org.piotrowski.cardureadoo.domain.security.UserRole;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.UserEntity;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.piotrowski.cardureadoo.web.dto.user.CreateUserRequest;
import org.piotrowski.cardureadoo.web.dto.user.CreateUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/bootstrap")
@RequiredArgsConstructor
public class BootstrapAdminController {

    private final UserApplicationService usersService;
    private final UserJpaRepository usersRepository;

    @Value("${app.security.bootstrap.token}")
    private String setupToken;

    @PostMapping(path="/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse createFirstAdmin(@RequestHeader("X-Setup-Token") String token, @RequestBody @Valid CreateUserRequest req) {
        if (usersRepository.countByRolesContaining(UserRole.ADMIN) > 0) {
            throw new IllegalStateException("Admin already exists");
        }
        if (setupToken == null || setupToken.isBlank() || !setupToken.equals(token)) {
            throw new SecurityException("Invalid setup token");
        }
        UserEntity u = usersService.createUser(req.username(), req.password(), Set.of(UserRole.ADMIN));
        return new CreateUserResponse(u.getId(), u.getUsername(), Set.of(UserRole.ADMIN.name()));
    }
}
