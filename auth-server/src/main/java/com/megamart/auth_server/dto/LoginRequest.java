package com.megamart.auth_server.dto;

public class LoginRequest {
    private String phoneNumber;
    private String password;

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}