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
                .route("user-admin-public", r -> r
                        .path("/api/users/register", "/api/auth/login")
                        .uri("lb://user-admin-server"))
                .route("user-admin-swagger", r -> r
                        .path("/user-admin-docs/**")
                        .filters(f -> f.rewritePath("/user-admin-docs/(?<segment>.*)", "/${segment}"))
                        .uri("lb://user-admin-server"))
                .route("user-admin-protected", r -> r
                        .path("/api/users/**", "/api/admin/**", "/api/addresses/**", "/api/payment-methods/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://user-admin-server"))
                .route("product-public", r -> r
                        .path("/api/products/**", "/api/categories/**")
                        .uri("lb://product-server"))
                .route("product-swagger", r -> r
                        .path("/product-docs/**")
                        .filters(f -> f.rewritePath("/product-docs/(?<segment>.*)", "/${segment}"))
                        .uri("lb://product-server"))
                .route("cart-protected", r -> r
                        .path("/api/cart/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://cart-server"))
                .route("cart-swagger", r -> r
                        .path("/cart-docs/**")
                        .filters(f -> f.rewritePath("/cart-docs/(?<segment>.*)", "/${segment}"))
                        .uri("lb://cart-server"))
                .route("order-payment-protected", r -> r
                        .path("/api/orders/**", "/api/payments/**", "/api/processing-locations/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://order-payment-server"))
                .route("order-payment-swagger", r -> r
                        .path("/order-payment-docs/**")
                        .filters(f -> f.rewritePath("/order-payment-docs/(?<segment>.*)", "/${segment}"))
                        .uri("lb://order-payment-server"))
                .build();
    }
}