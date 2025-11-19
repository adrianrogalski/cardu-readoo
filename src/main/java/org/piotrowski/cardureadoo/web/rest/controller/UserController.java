package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.application.service.UserApplicationService;
import org.piotrowski.cardureadoo.domain.security.UserRole;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.UserEntity;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.piotrowski.cardureadoo.web.dto.user.CreateUserRequest;
import org.piotrowski.cardureadoo.web.dto.user.CreateUserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService usersService;
    private final UserJpaRepository userRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CreateUserResponse create(@RequestBody @Valid CreateUserRequest req) {
        Set<UserRole> roles = req.roles() == null ? null :
                req.roles().stream().map(String::toUpperCase).map(UserRole::valueOf).collect(Collectors.toSet());
        UserEntity u = usersService.createUser(req.username(), req.password(), roles);
        return new CreateUserResponse(u.getId(), u.getUsername(),
                u.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));
    }
}
