package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.*;
import com.megamart.useradminserver.entity.Admin;
import com.megamart.useradminserver.entity.User;
import com.megamart.useradminserver.exception.DuplicateResourceException;
import com.megamart.useradminserver.exception.ResourceNotFoundException;
import com.megamart.useradminserver.repository.AdminRepository;
import com.megamart.useradminserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto adminLogin(AdminLoginDto adminLoginDto) {
        Admin admin = adminRepository.findByEmail(adminLoginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!passwordEncoder.matches(adminLoginDto.getPassword(), admin.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        String token = jwtService.generateToken(admin.getAdminId(), admin.getRole().name());
        return new AuthResponseDto(token, admin.getAdminId(), admin.getName(), admin.getEmail(), admin.getRole().name(), "Login successful");
    }

    @Override
    public Admin findByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void updateLastLogin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);
    }

    @Override
    public Admin createAdmin(AdminCreateDto adminCreateDto) {
        if (adminRepository.existsByEmail(adminCreateDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Admin admin = new Admin();
        admin.setAdminId(generateAdminId());
        admin.setName(adminCreateDto.getName());
        admin.setEmail(adminCreateDto.getEmail());
        admin.setPassword(passwordEncoder.encode(adminCreateDto.getPassword()));
        Admin.AdminRole adminRole = Admin.AdminRole.valueOf(adminCreateDto.getRole());
        admin.setRole(adminRole);
        admin.setPermissions(getPermissionsForRole(adminRole));
        
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    @Override
    public Admin updateProfile(Long adminId, AdminProfileUpdateDto updateDto) {
        Admin admin = findById(adminId);
        
        // Check if email is being changed and if it already exists
        if (!admin.getEmail().equals(updateDto.getEmail()) && 
            adminRepository.existsByEmail(updateDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        admin.setName(updateDto.getName());
        admin.setEmail(updateDto.getEmail());
        if (updateDto.getPhotoUrl() != null) {
            admin.setPhotoUrl(updateDto.getPhotoUrl());
        }
        
        return adminRepository.save(admin);
    }

    @Override
    public Admin changePassword(Long adminId, AdminPasswordChangeDto passwordDto) {
        Admin admin = findById(adminId);
        
        // Verify current password
        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), admin.getPassword())) {
            throw new ResourceNotFoundException("Current password is incorrect");
        }
        
        // Update password
        admin.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        
        return adminRepository.save(admin);
    }

    @Override
    public Admin findByAdminId(String adminId) {
        return adminRepository.findByAdminId(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
    }

    @Override
    public Admin updateProfileByAdminId(String adminId, AdminProfileUpdateDto updateDto) {
        Admin admin = findByAdminId(adminId);
        
        // Update name if provided
        if (updateDto.getName() != null && !updateDto.getName().trim().isEmpty()) {
            admin.setName(updateDto.getName());
        }
        
        // Update email if provided and check for duplicates
        if (updateDto.getEmail() != null && !updateDto.getEmail().trim().isEmpty()) {
            if (!admin.getEmail().equals(updateDto.getEmail()) && 
                adminRepository.existsByEmail(updateDto.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            admin.setEmail(updateDto.getEmail());
        }
        
        // Update photo URL if provided
        if (updateDto.getPhotoUrl() != null) {
            admin.setPhotoUrl(updateDto.getPhotoUrl());
        }
        
        return adminRepository.save(admin);
    }

    @Override
    public Admin changePasswordByAdminId(String adminId, AdminPasswordChangeDto passwordDto) {
        Admin admin = findByAdminId(adminId);
        
        // Verify current password
        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), admin.getPassword())) {
            throw new ResourceNotFoundException("Current password is incorrect");
        }
        
        // Update password
        admin.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
        
        return adminRepository.save(admin);
    }

    private String generateAdminId() {
        String adminId;
        do {
            adminId = String.valueOf(100000 + new Random().nextInt(900000));
        } while (adminRepository.existsByAdminId(adminId));
        return adminId;
    }

    private List<Admin.AdminPermission> getPermissionsForRole(Admin.AdminRole role) {
        switch (role) {
            case super_admin:
                return Arrays.asList(Admin.AdminPermission.values());
            case admin:
                return Arrays.asList(Admin.AdminPermission.manage_products, Admin.AdminPermission.manage_orders, Admin.AdminPermission.manage_customers, Admin.AdminPermission.view_analytics);
            case product_manager:
                return Arrays.asList(Admin.AdminPermission.manage_products, Admin.AdminPermission.view_analytics);
            case customer_manager:
                return Arrays.asList(Admin.AdminPermission.manage_customers, Admin.AdminPermission.view_analytics);
            case order_manager:
                return Arrays.asList(Admin.AdminPermission.manage_orders, Admin.AdminPermission.view_analytics);
            default:
                return Arrays.asList(Admin.AdminPermission.view_analytics);
        }
    }
}