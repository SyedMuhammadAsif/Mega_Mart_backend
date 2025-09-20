package com.megamart.auth_server.service;

import com.megamart.auth_server.dto.AuthResponse;
import com.megamart.auth_server.dto.LoginRequest;
import com.megamart.auth_server.dto.RegisterRequest;
import com.megamart.auth_server.dto.RegisterResponse;
import com.megamart.auth_server.dto.ValidationResponse;
import com.megamart.auth_server.entity.User;
import com.megamart.auth_server.exception.AuthException;
import com.megamart.auth_server.exception.UserAlreadyExistsException;
import com.megamart.auth_server.repository.UserRepository;
import com.megamart.auth_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public RegisterResponse register(RegisterRequest request) {
        if (request == null) {
            throw new AuthException("Request body is required");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new AuthException("Name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new AuthException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new AuthException("Password is required");
        }
        if (request.getRole() == null) {
            throw new AuthException("Role is required");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User savedUser = userRepository.save(user);
        return new RegisterResponse(savedUser, "User registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new AuthException("Request body is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new AuthException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new AuthException("Password is required");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().toString());
        return new AuthResponse(token, "Login successful", user.getId());
    }

    public ValidationResponse validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new AuthException("Authorization header is required");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            throw new AuthException("Invalid token");
        }

        String email = jwtUtil.getEmailFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        return new ValidationResponse(true, "Token is valid", email, role);
    }
}