package org.piotrowski.cardureadoo.infrastructure.security.session;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryTokenStore {

    private final Map<String, UserDetails> sessions = new ConcurrentHashMap<>();

    public String createSession(UserDetails user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        return token;
    }

    public UserDetails getUser(String token) {
        return sessions.get(token);
    }

    public void invalidate(String token) {
        sessions.remove(token);
    }
}
