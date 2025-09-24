package com.megamart.orderpaymentserver.controller;

import com.megamart.orderpaymentserver.dto.PaymentRequest;
import com.megamart.orderpaymentserver.dto.OrderResponse;
import com.megamart.orderpaymentserver.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/process")
    public ResponseEntity<OrderResponse.Payment> processPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        OrderResponse.Payment paymentResponse = paymentService.processPayment(request);
        return ResponseEntity.ok(paymentResponse);
    }
    
    @PostMapping("/refund/{orderId}")
    public ResponseEntity<Map<String, Object>> refundPayment(@PathVariable Long orderId) {
        log.info("Processing refund for order: {}", orderId);
        
        OrderResponse.Payment payment = paymentService.getPaymentByOrderId(orderId);
        
        // Check if payment can be refunded
        if (!"COMPLETED".equals(payment.getPaymentStatus())) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Cannot refund payment");
            error.put("reason", "Payment status is: " + payment.getPaymentStatus());
            return ResponseEntity.badRequest().body(error);
        }
        
        // Simulate refund processing based on payment type
        Map<String, Object> refundResult = processRefundByPaymentType(payment);
        
        return ResponseEntity.ok(refundResult);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse.Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        OrderResponse.Payment paymentResponse = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(paymentResponse);
    }
    
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<OrderResponse.Payment> getPaymentByTransactionId(@PathVariable String transactionId) {
        OrderResponse.Payment paymentResponse = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(paymentResponse);
    }
    
    private Map<String, Object> processRefundByPaymentType(OrderResponse.Payment payment) {
        Map<String, Object> refundResult = new HashMap<>();
        String paymentType = payment.getPaymentMethod().getType();
        
        refundResult.put("orderId", payment.getId());
        refundResult.put("originalTransactionId", payment.getTransactionId());
        refundResult.put("paymentType", paymentType);
        refundResult.put("refundAmount", "Amount will be refunded");
        
        switch (paymentType) {
            case "CARD":
                refundResult.put("refundMethod", "Credit Card Refund");
                refundResult.put("refundTime", "3-5 business days");
                refundResult.put("refundTo", "Original card ending in " + 
                    payment.getPaymentMethod().getCardNumber().substring(
                        payment.getPaymentMethod().getCardNumber().length() - 4));
                refundResult.put("message", "Refund initiated to your card. It will appear in 3-5 business days.");
                break;
                
            case "UPI":
                refundResult.put("refundMethod", "UPI Refund");
                refundResult.put("refundTime", "Instant to 2 hours");
                refundResult.put("refundTo", payment.getPaymentMethod().getUpiId());
                refundResult.put("message", "Refund initiated to your UPI account. It should appear within 2 hours.");
                break;
                
            case "COD":
                refundResult.put("refundMethod", "No refund needed");
                refundResult.put("refundTime", "N/A");
                refundResult.put("message", "No refund needed for Cash on Delivery orders.");
                break;
                
            default:
                refundResult.put("message", "Refund method not supported for this payment type.");
        }
        
        return refundResult;
    }
} 
