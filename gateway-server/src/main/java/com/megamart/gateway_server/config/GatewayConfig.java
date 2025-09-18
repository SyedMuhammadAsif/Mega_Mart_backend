package com.megamart.gateway_server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.megamart.gateway_server.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-public", r -> r
                        .path("/api/auth/register", "/api/auth/login", "/api/auth/validate")
                        .uri("lb://auth-server"))
                .route("auth-swagger", r -> r
                        .path("/auth-docs/**")
                        .filters(f -> f.rewritePath("/auth-docs/(?<segment>.*)", "/${segment}"))
                        .uri("lb://auth-server"))
                .route("auth-protected", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://auth-server"))
                .build();
    }
}