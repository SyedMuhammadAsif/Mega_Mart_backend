package com.megamart.orderpaymentserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String userId;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String role;
}
