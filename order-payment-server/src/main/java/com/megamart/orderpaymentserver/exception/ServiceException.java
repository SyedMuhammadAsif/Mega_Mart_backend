package com.megamart.orderpaymentserver.exception;

public class ServiceException extends RuntimeException {
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(String operation, String details) {
        super("Service error during " + operation + ": " + details);
    }
} 
