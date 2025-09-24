package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.*;
import com.megamart.useradminserver.entity.User;
import com.megamart.useradminserver.exception.ResourceNotFoundException;
import com.megamart.useradminserver.exception.DuplicateResourceException;
import com.megamart.useradminserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserProfile(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
    }

    @Override
    public User registerUser(UserRegistrationDto registrationDto) {
        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + registrationDto.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + registrationDto.getEmail());
        }

        User user = new User();
        user.setUserId(generateUserId());
        user.setName(null); // Will be set during profile update
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setPhone(null); // Will be set during profile update
        user.setDateOfBirth(null); // Will be set during profile update
        user.setGender(null); // Will be set during profile update
        user.setRole(User.Role.customer); // Automatically set as customer

        return userRepository.save(user);
    }

    @Override
    public AuthResponseDto loginUser(LoginDto loginDto) {
        // Try to find user by email first, then by username
        User user = userRepository.findByEmail(loginDto.getEmail())
                .or(() -> userRepository.findByUsername(loginDto.getEmail()))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username/email or password"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid username/email or password");
        }

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());
        return new AuthResponseDto(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole().name(), "Login successful");
    }

    @Override
    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with userId: " + userId));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User createUser(UserRegistrationDto registrationDto) {
        return registerUser(registrationDto);
    }

    @Override
    public User updateUser(Long id, UserUpdateDto updateDto) {
        User user = getUserById(id);
        
        if (updateDto.getName() != null) user.setName(updateDto.getName());
        if (updateDto.getPhone() != null) user.setPhone(updateDto.getPhone());
        if (updateDto.getDateOfBirth() != null) user.setDateOfBirth(updateDto.getDateOfBirth());
        if (updateDto.getGender() != null) user.setGender(updateDto.getGender());

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public void resetPassword(PasswordResetDto resetDto) {
        User user = userRepository.findByEmail(resetDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + resetDto.getEmail()));

        // Validate old password
        if (!passwordEncoder.matches(resetDto.getOldPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
        userRepository.save(user);
    }

    private String generateUserId() {
        String userId;
        do {
            userId = UUID.randomUUID().toString().substring(0, 10);
        } while (userRepository.existsByUserId(userId));
        return userId;
    }
}