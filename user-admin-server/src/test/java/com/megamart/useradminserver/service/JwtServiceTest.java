package com.megamart.useradminserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "testSecretKey123456789012345678901234567890123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void testTokenGeneration() {
        String token = jwtService.generateToken("user123", "test@example.com", "customer");
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void testTokenValidation() {
        String token = jwtService.generateToken("user123", "test@example.com", "customer");
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void testTokenBlacklisting() {
        String token = jwtService.generateToken("user123", "test@example.com", "customer");
        assertTrue(jwtService.validateToken(token));
        
        jwtService.blacklistToken(token);
        assertFalse(jwtService.validateToken(token));
        assertTrue(jwtService.isTokenBlacklisted(token));
    }

    @Test
    void testExtractUsername() {
        String token = jwtService.generateToken("user123", "test@example.com", "customer");
        String extractedEmail = jwtService.extractUsername(token);
        assertEquals("test@example.com", extractedEmail);
    }
}