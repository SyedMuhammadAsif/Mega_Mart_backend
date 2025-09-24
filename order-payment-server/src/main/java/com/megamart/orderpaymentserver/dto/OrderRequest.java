package com.megamart.orderpaymentserver.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    
    @NotNull(message = "User ID is required")
    private String userId;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal total;
    
    @NotNull(message = "Payment type is required")
    @Pattern(regexp = "^(CARD|UPI|COD)$", message = "Payment type must be CARD, UPI, or COD")
    private String paymentType;
    
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItem> items;
    
    // Address - either use existing (provide ID) or create new (provide details)
    private Long addressId;
    private Address newAddress;
    
    // Payment method - either use existing (provide ID) or create new (provide details)
    private Long paymentMethodId;
    private PaymentMethod newPaymentMethod;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotNull(message = "Line total is required")
        @Positive(message = "Line total must be positive")
        private BigDecimal lineTotal;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        @Pattern(regexp = "^.{1,100}$", message = "Full name is required")
        private String fullName;
        
        @Pattern(regexp = "^.{1,200}$", message = "Address line 1 is required")
        private String addressLine1;
        
        private String addressLine2;
        private String city;
        private String state;
        
        @Pattern(regexp = "^[0-9]{5,10}$", message = "Postal code must be 5-10 digits")
        private String postalCode;
        
        private String country;
        
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
        private String phone;
        
        private Boolean isDefault;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentMethod {
        @Pattern(regexp = "^(CARD|UPI|COD)$", message = "Payment type must be CARD, UPI, or COD")
        private String type;
        
        // For CARD payments
        @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be exactly 16 digits")
        private String cardNumber;
        private String cardholderName;
        
        @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiry month must be 01-12")
        private String expiryMonth;
        
        @Pattern(regexp = "^20[2-9][0-9]$", message = "Expiry year must be 2020-2099")
        private String expiryYear;
        
        @Pattern(regexp = "^[0-9]{3}$", message = "CVV must be exactly 3 digits")
        private String cvv;
        
        // For UPI payments
        @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$", message = "Invalid UPI ID format")
        private String upiId;
        
        private Boolean isDefault;
    }
} 
