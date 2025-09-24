package com.megamart.useradminserver.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String token;
    private String userId;
    private String name;
    private String email;
    private String role;
    private String message;
}