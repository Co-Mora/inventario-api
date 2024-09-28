package com.intercop.inventario.auth.service;
import com.intercop.inventario.auth.model.User;
import com.intercop.inventario.auth.repository.UserRepository;
import com.intercop.inventario.auth.security.JwtTokenProvider;
import com.intercop.inventario.common.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Mono<String> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> jwtTokenProvider.createToken(user.getUsername()));
    }

    public Mono<Object> register(RegisterRequest registerRequest) {
        return userRepository.findByUsername(registerRequest.getUsername())
                .flatMap(existingUser -> Mono.error(new RuntimeException("Username already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    User newUser = new User();
                    newUser.setUsername(registerRequest.getUsername());
                    newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                    newUser.setEmail(registerRequest.getEmail());
                    newUser.setRoles(Collections.singletonList("ROLE_USER"));
                    return userRepository.save(newUser);
                }));
    }

    public Mono<User> getCurrentUser(String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        return userRepository.findByUsername(username);
    }

    public Mono<String> refreshToken(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            return userRepository.findByUsername(username)
                    .map(user -> jwtTokenProvider.createToken(user.getUsername()));
        }
        return Mono.error(new RuntimeException("Invalid refresh token"));
    }

    private String generateToken(User user) {
        return jwtTokenProvider.createToken(user.getUsername());
    }
}