package com.intercop.inventario.auth.controller;
import com.intercop.inventario.auth.model.User;
import com.intercop.inventario.auth.service.AuthService;
import com.intercop.inventario.common.dto.ApiResponse;
import com.intercop.inventario.common.dto.LoginRequest;
import com.intercop.inventario.common.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<String>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                .map(jwt -> ResponseEntity.ok(new ApiResponse<>(true, "Login successful", jwt)))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid credentials", null)));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<ApiResponse<Object>>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(true, "User registered successfully", user)))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, e.getMessage(), null))));
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<ApiResponse<User>>> getCurrentUser(@RequestHeader("Authorization") String token) {
        return authService.getCurrentUser(token)
                .map(user -> ResponseEntity.ok(new ApiResponse<>(true, "User details retrieved successfully", user)))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "User not authenticated", null)));
    }

    @PostMapping("/refresh-token")
    public Mono<ResponseEntity<ApiResponse<String>>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        return authService.refreshToken(refreshToken)
                .map(newToken -> ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", newToken)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, e.getMessage(), null))));
    }
}