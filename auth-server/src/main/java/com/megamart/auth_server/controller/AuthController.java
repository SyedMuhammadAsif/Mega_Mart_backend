package com.megamart.auth_server.controller;

import com.megamart.auth_server.dto.*;
import com.megamart.auth_server.entity.User;
import com.megamart.auth_server.repository.UserRepository;
import com.megamart.auth_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
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