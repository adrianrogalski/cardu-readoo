package org.piotrowski.cardureadoo.web.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.infrastructure.security.session.InMemoryTokenStore;
import org.piotrowski.cardureadoo.web.dto.user.LoginRequest;
import org.piotrowski.cardureadoo.web.dto.user.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final InMemoryTokenStore tokenStore;

    // POST - login endpoint
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            UserDetails principal = (UserDetails) auth.getPrincipal();

            String username = principal.getUsername();
            Set<String> roles = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            String token = tokenStore.createSession(principal);

            return new LoginResponse(username, roles, token);
        } catch (BadCredentialsException ex) {
            throw ex;
        }
    }
}
