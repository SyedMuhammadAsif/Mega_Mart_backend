package com.megamart.useradminserver.config;

import com.megamart.useradminserver.entity.Admin;
import com.megamart.useradminserver.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default super admin if not exists
        if (!adminRepository.existsByEmail("superadmin@megamart.com")) {
            Admin superAdmin = new Admin();
            superAdmin.setAdminId("100001");
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("superadmin@megamart.com");
            superAdmin.setPassword(passwordEncoder.encode("admin123"));
            superAdmin.setRole(Admin.AdminRole.super_admin);
            superAdmin.setPermissions(Arrays.asList(Admin.AdminPermission.values()));
            
            adminRepository.save(superAdmin);
            System.out.println("Default super admin created: superadmin@megamart.com / admin123");
        }

        // Create default admin if not exists
        if (!adminRepository.existsByEmail("admin@megamart.com")) {
            Admin admin = new Admin();
            admin.setAdminId("100002");
            admin.setName("Admin User");
            admin.setEmail("admin@megamart.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Admin.AdminRole.admin);
            admin.setPermissions(Arrays.asList(
                Admin.AdminPermission.manage_products,
                Admin.AdminPermission.manage_orders,
                Admin.AdminPermission.manage_customers,
                Admin.AdminPermission.view_analytics
            ));
            
            adminRepository.save(admin);
            System.out.println("Default admin created: admin@megamart.com / admin123");
        }

        // Create product manager if not exists
        if (!adminRepository.existsByEmail("productmanager@megamart.com")) {
            Admin productManager = new Admin();
            productManager.setAdminId("100003");
            productManager.setName("Product Manager");
            productManager.setEmail("productmanager@megamart.com");
            productManager.setPassword(passwordEncoder.encode("admin123"));
            productManager.setRole(Admin.AdminRole.product_manager);
            productManager.setPermissions(Arrays.asList(
                Admin.AdminPermission.manage_products,
                Admin.AdminPermission.view_analytics
            ));
            
            adminRepository.save(productManager);
            System.out.println("Product manager created: productmanager@megamart.com / admin123");
        }

        // Create customer manager if not exists
        if (!adminRepository.existsByEmail("customermanager@megamart.com")) {
            Admin customerManager = new Admin();
            customerManager.setAdminId("100004");
            customerManager.setName("Customer Manager");
            customerManager.setEmail("customermanager@megamart.com");
            customerManager.setPassword(passwordEncoder.encode("admin123"));
            customerManager.setRole(Admin.AdminRole.customer_manager);
            customerManager.setPermissions(Arrays.asList(
                Admin.AdminPermission.manage_customers,
                Admin.AdminPermission.view_analytics
            ));
            
            adminRepository.save(customerManager);
            System.out.println("Customer manager created: customermanager@megamart.com / admin123");
        }

        // Create order manager if not exists
        if (!adminRepository.existsByEmail("ordermanager@megamart.com")) {
            Admin orderManager = new Admin();
            orderManager.setAdminId("100005");
            orderManager.setName("Order Manager");
            orderManager.setEmail("ordermanager@megamart.com");
            orderManager.setPassword(passwordEncoder.encode("admin123"));
            orderManager.setRole(Admin.AdminRole.order_manager);
            orderManager.setPermissions(Arrays.asList(
                Admin.AdminPermission.manage_orders,
                Admin.AdminPermission.view_analytics
            ));
            
            adminRepository.save(orderManager);
            System.out.println("Order manager created: ordermanager@megamart.com / admin123");
        }
    }
}