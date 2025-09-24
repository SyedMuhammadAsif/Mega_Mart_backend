package com.megamart.orderpaymentserver.exception;

public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException(Long orderId) {
        super("Payment not found for order id: " + orderId);
    }
    
    public PaymentNotFoundException(String transactionId, boolean isTransactionId) {
        super("Payment not found for transaction id: " + transactionId);
    }
    
    public PaymentNotFoundException(String message) {
        super(message);
    }
} 
