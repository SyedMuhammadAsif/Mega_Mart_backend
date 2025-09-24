package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.*;
import com.megamart.useradminserver.entity.Admin;

import java.util.List;

public interface AdminService {
    AuthResponseDto adminLogin(AdminLoginDto adminLoginDto);
    Admin findByEmail(String email);
    Admin findById(Long id);
    Admin findByAdminId(String adminId);
    void updateLastLogin(Long adminId);
    Admin createAdmin(AdminCreateDto adminCreateDto);
    List<Admin> getAllAdmins();
    Admin updateProfile(Long adminId, AdminProfileUpdateDto updateDto);
    Admin changePassword(Long adminId, AdminPasswordChangeDto passwordDto);
    Admin updateProfileByAdminId(String adminId, AdminProfileUpdateDto updateDto);
    Admin changePasswordByAdminId(String adminId, AdminPasswordChangeDto passwordDto);
}