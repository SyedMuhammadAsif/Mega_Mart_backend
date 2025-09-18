package com.megamart.auth_server.dto;

public class ValidationResponse {
    private boolean valid;
    private String message;
    private String username;
    private String role;

    public ValidationResponse(boolean valid, String message, String username, String role) {
        this.valid = valid;
        this.message = message;
        this.username = username;
        this.role = role;
    }

    public ValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
        this.username = null;
        this.role = null;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
} 