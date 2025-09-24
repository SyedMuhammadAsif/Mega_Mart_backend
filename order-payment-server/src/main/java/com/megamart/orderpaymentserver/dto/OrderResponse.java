package com.megamart.orderpaymentserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    private String userId;
    private BigDecimal total;
    private String paymentType;
    private String orderStatus;
    private String paymentStatus;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Address shippingAddress;
    private List<OrderItem> orderItems;
    private Payment payment;
    
    // Customer information
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        private Long id;
        private Long productId;
        private Integer quantity;
        private BigDecimal lineTotal;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        private Long id;
        private String fullName;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String phone;
        private Boolean isDefault;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentMethod {
        private Long id;
        private String type;
        private String cardNumber; // masked
        private String cardholderName;
        private String expiryMonth;
        private String expiryYear;
        private String upiId;
        private Boolean isDefault;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Payment {
        private Long id;
        private String paymentStatus;
        private LocalDateTime paymentDate;
        private String transactionId;
        private PaymentMethod paymentMethod;
    }
} 
