package com.megamart.useradminserver.controller;

import com.megamart.useradminserver.dto.*;
import com.megamart.useradminserver.entity.Admin;
import com.megamart.useradminserver.entity.User;
import com.megamart.useradminserver.service.AdminService;
import com.megamart.useradminserver.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "APIs for admin authentication")
public class AdminController {

    private final AdminService adminService;
    private final JwtService jwtService;

    @PostMapping("/admin/login")
    @Operation(summary = "Admin login", description = "Authenticate admin user and return access token")
    public ResponseEntity<AuthResponseDto> adminLogin(@Valid @RequestBody AdminLoginDto adminLoginDto) {
        AuthResponseDto response = adminService.adminLogin(adminLoginDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/logout")
    @Operation(summary = "Admin logout", description = "Logout admin and invalidate token")
    public ResponseEntity<MessageDto> logoutAdmin(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtService.blacklistToken(token);
        }
        return ResponseEntity.ok(new MessageDto("Admin logged out successfully"));
    }

    @GetMapping("/admin/{adminId}")
    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Get admin by adminId", description = "Retrieve admin details by adminId")
    public ResponseEntity<Admin> getAdminByAdminId(@PathVariable String adminId) {
        Admin admin = adminService.findByAdminId(adminId);
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/admin/{adminId}")
    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Update admin profile", description = "Update admin profile information")
    public ResponseEntity<Admin> updateAdminProfile(@PathVariable String adminId, @Valid @RequestBody AdminProfileUpdateDto updateDto) {
        Admin updatedAdmin = adminService.updateProfileByAdminId(adminId, updateDto);
        return ResponseEntity.ok(updatedAdmin);
    }

    @PutMapping("/admin/{adminId}/password")
    @CrossOrigin(origins = "http://localhost:4200")
    @Operation(summary = "Change admin password", description = "Change admin password")
    public ResponseEntity<MessageDto> changeAdminPassword(@PathVariable String adminId, @Valid @RequestBody AdminPasswordChangeDto passwordDto) {
        adminService.changePasswordByAdminId(adminId, passwordDto);
        return ResponseEntity.ok(new MessageDto("Password changed successfully"));
    }
}