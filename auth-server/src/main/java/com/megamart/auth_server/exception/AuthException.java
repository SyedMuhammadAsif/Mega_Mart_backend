package com.megamart.auth_server.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}