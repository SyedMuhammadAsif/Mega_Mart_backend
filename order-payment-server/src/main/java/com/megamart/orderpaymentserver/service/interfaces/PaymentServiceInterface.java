package com.megamart.orderpaymentserver.service.interfaces;

import com.megamart.orderpaymentserver.dto.PaymentRequest;
import com.megamart.orderpaymentserver.dto.OrderResponse;

/**
 * Interface for Payment Service
 * This defines what methods the PaymentService should have
 */
public interface PaymentServiceInterface {
    
    /**
     * Process payment for an order
     * @param request - Payment details
     * @return Payment result
     */
    OrderResponse.Payment processPayment(PaymentRequest request);
    
    /**
     * Get payment details by order ID
     * @param orderId - Order ID
     * @return Payment details
     */
    OrderResponse.Payment getPaymentByOrderId(Long orderId);
    
    /**
     * Get payment details by transaction ID
     * @param transactionId - Transaction ID
     * @return Payment details
     */
    OrderResponse.Payment getPaymentByTransactionId(String transactionId);
} 
