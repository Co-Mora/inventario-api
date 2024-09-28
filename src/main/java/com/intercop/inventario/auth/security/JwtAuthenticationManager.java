package com.intercop.inventario.auth.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationManager(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String username;
        try {
            username = tokenProvider.getUsernameFromToken(authToken);
        } catch (Exception e) {
            username = null;
        }
        if (username != null && tokenProvider.validateToken(authToken)) {
            Authentication auth = tokenProvider.getAuthentication(authToken);
            return Mono.just(auth);
        } else {
            return Mono.empty();
        }
    }
}