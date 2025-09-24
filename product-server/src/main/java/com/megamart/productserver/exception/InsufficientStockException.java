package com.megamart.productserver.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {

        super(message);
    }
    
    public InsufficientStockException(Long productId, int requested, int available) {
        super("Insufficient stock for product " + productId + ". Requested: " + requested + ", Available: " + available);
    }
}
