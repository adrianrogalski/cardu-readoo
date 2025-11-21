package org.piotrowski.cardureadoo.infrastructure.security.config;

import lombok.RequiredArgsConstructor;
import org.piotrowski.cardureadoo.domain.security.UserRole;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities.UserEntity;
import org.piotrowski.cardureadoo.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserJpaRepository userRepository;
    private final Environment env;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserEntity u = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new User(u.getUsername(), u.getPasswordHash(), u.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                    .collect(Collectors.toSet()));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> enc = new HashMap<>();
        enc.put("argon2", new Argon2PasswordEncoder(16,32,1,1<<14,3));
        enc.put("bcrypt", new BCryptPasswordEncoder(12));
        return new DelegatingPasswordEncoder("argon2", enc);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .headers(h -> h
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .contentTypeOptions(c -> {})
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                        .frameOptions(f -> f.sameOrigin())
                )
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(
                                "/", "/index.html",
                                "/favicon.ico",
                                "/assets/**",
                                "/*.js", "/*.css", "/*.map",
                                "/robots.txt"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/bootstrap/admin").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/users/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.POST,   "/api/expansions/**", "/api/cards/**", "/api/offers/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT,    "/api/expansions/**", "/api/cards/**", "/api/offers/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH,  "/api/expansions/**", "/api/cards/**", "/api/offers/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/expansions/**", "/api/cards/**", "/api/offers/**").hasRole(UserRole.ADMIN.name())
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = new ArrayList<>();
        for (int i = 1; ; i++) {
            String key = "app.allowed.origin-" + i;
            String val = env.getProperty(key);
            if (val == null) break;
            origins.add(val);
        }

        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
