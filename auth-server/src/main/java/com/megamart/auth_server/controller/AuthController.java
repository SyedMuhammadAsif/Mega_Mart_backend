package com.megamart.auth_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megamart.auth_server.dto.AuthResponse;
import com.megamart.auth_server.dto.LoginRequest;
import com.megamart.auth_server.dto.RegisterRequest;
import com.megamart.auth_server.dto.RegisterResponse;
import com.megamart.auth_server.dto.ValidationResponse;
import com.megamart.auth_server.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidationResponse> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
        ValidationResponse response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}