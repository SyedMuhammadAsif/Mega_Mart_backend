package com.megamart.useradminserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserAdminServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(
                UserAdminServerApplication.class, args);
        System.out.println("User Admin Server running successfully");
    }
}