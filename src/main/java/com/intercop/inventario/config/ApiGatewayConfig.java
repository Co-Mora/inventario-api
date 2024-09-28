package com.intercop.inventario.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("products", r -> r.path("/api/products/**")
                        .uri("http://localhost:8081"))
                .route("notifications", r -> r.path("/api/notifications/**")
                        .uri("http://localhost:8082"))
                .route("auth", r -> r.path("/api/auth/**")
                        .uri("http://localhost:8083"))
                .build();
    }
}