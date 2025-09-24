package com.megamart.useradminserver.repository;

import com.megamart.useradminserver.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByAdminId(String adminId);
    boolean existsByEmail(String email);
    boolean existsByAdminId(String adminId);
}