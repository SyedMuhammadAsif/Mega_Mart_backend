package com.megamart.useradminserver.controller;

import com.megamart.useradminserver.dto.*;
import com.megamart.useradminserver.entity.User;
import com.megamart.useradminserver.entity.Admin;
import com.megamart.useradminserver.service.UserService;
import com.megamart.useradminserver.service.AdminService;
import com.megamart.useradminserver.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        try {
            User user = userService.findByUsername(loginDto.getEmail());
            if (user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getUserId(), "customer");
                return ResponseEntity.ok(new AuthResponseDto(
                    token, user.getUserId(), user.getName(), user.getEmail(), "customer", "Login successful"
                ));
            }
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, null, null, null, "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, null, null, null, "Login failed"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.createUser(registrationDto);
            String token = jwtService.generateToken(user.getUserId(), "customer");
            return ResponseEntity.ok(new AuthResponseDto(
                token, user.getUserId(), user.getName(), user.getEmail(), "customer", "Registration successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/admin-login")
    public ResponseEntity<AuthResponseDto> adminLogin(@RequestBody AdminLoginDto loginDto) {
        try {
            Admin admin = adminService.findByEmail(loginDto.getEmail());
            if (admin != null && passwordEncoder.matches(loginDto.getPassword(), admin.getPassword())) {
                String token = jwtService.generateToken(admin.getAdminId(), admin.getRole().toString());
                adminService.updateLastLogin(admin.getId());
                return ResponseEntity.ok(new AuthResponseDto(
                    token, admin.getAdminId(), admin.getName(), admin.getEmail(), admin.getRole().toString(), "Admin login successful"
                ));
            }
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, null, null, null, "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, null, null, null, "Admin login failed"));
        }
    }
}