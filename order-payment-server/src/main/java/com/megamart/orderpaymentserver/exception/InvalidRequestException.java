package com.megamart.orderpaymentserver.exception;

public class InvalidRequestException extends RuntimeException {
    
    public InvalidRequestException(String message) {
        super(message);
    }
    
    public InvalidRequestException(String field, String reason) {
        super(String.format("Invalid %s: %s", field, reason));
    }
} 
