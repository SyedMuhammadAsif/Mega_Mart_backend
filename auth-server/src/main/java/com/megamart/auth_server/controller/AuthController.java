package com.megamart.auth_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.megamart.auth_server.dto.AuthResponse;
import com.megamart.auth_server.dto.LoginRequest;
import com.megamart.auth_server.dto.RegisterRequest;
import com.megamart.auth_server.dto.RegisterResponse;
import com.megamart.auth_server.entity.User;
import com.megamart.auth_server.repository.UserRepository;
import com.megamart.auth_server.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User savedUser = userRepository.save(user);
        RegisterResponse response = new RegisterResponse(savedUser, "User registered successfully");
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, "Invalid credentials", null));
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        return ResponseEntity.ok(new AuthResponse(token, "Login successful", user.getId()));
    }

    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    public ResponseEntity<String> validateToken(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Authorization header missing");
        }
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            if (jwtUtil.validateToken(token)) {
                return ResponseEntity.ok("Token is valid");
            }
            return ResponseEntity.badRequest().body("Invalid token");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Token validation error: " + e.getMessage());
        }
    }
}