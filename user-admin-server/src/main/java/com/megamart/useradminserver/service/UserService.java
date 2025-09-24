package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.*;
import com.megamart.useradminserver.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByUserId(String userId);
    User getUserProfile(String userId);
    User registerUser(UserRegistrationDto registrationDto);
    User createUser(UserRegistrationDto registrationDto);
    AuthResponseDto loginUser(LoginDto loginDto);
    User findByUsername(String username);
    User updateUser(Long id, UserUpdateDto updateDto);
    void deleteUser(Long id);
    void resetPassword(PasswordResetDto resetDto);
}