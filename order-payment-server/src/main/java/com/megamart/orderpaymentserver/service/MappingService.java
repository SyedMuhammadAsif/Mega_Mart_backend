package com.megamart.orderpaymentserver.service;

import com.megamart.orderpaymentserver.dto.OrderResponse;
import com.megamart.orderpaymentserver.entity.Order;
import com.megamart.orderpaymentserver.entity.OrderItem;
import com.megamart.orderpaymentserver.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MappingService {
    
    public OrderResponse mapToOrderResponse(Order order, OrderResponse.Address shippingAddress, OrderResponse.PaymentMethod paymentMethod) {
        // Use stored shipping address if available, otherwise use provided address
        OrderResponse.Address finalShippingAddress = shippingAddress;
        if (order.getShippingFullName() != null) {
            finalShippingAddress = OrderResponse.Address.builder()
                    .id(order.getShippingAddressId())
                    .fullName(order.getShippingFullName())
                    .addressLine1(order.getShippingAddressLine1())
                    .addressLine2(order.getShippingAddressLine2())
                    .city(order.getShippingCity())
                    .state(order.getShippingState())
                    .postalCode(order.getShippingPostalCode())
                    .country(order.getShippingCountry())
                    .phone(order.getShippingPhone())
                    .build();
        }
        
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .total(order.getTotal())
                .paymentType(order.getPaymentType().name())
                .orderStatus(order.getOrderStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .orderDate(order.getOrderDate())
                .shippingAddress(finalShippingAddress)
                .orderItems(mapOrderItems(order.getOrderItems()))
                .payment(mapPayment(order.getPayment(), paymentMethod))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    public OrderResponse.Payment mapToPaymentResponse(Payment payment, OrderResponse.PaymentMethod paymentMethod) {
        return OrderResponse.Payment.builder()
                .id(payment.getId())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentDate(payment.getPaymentDate())
                .transactionId(payment.getTransactionId())
                .paymentMethod(paymentMethod)
                .build();
    }
    
    private List<OrderResponse.OrderItem> mapOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }
        
        return orderItems.stream()
                .map(item -> OrderResponse.OrderItem.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .lineTotal(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());
    }
    
    private OrderResponse.Payment mapPayment(Payment payment, OrderResponse.PaymentMethod paymentMethod) {
        if (payment == null) {
            return null;
        }
        
        return OrderResponse.Payment.builder()
                .id(payment.getId())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentDate(payment.getPaymentDate())
                .transactionId(payment.getTransactionId())
                .paymentMethod(paymentMethod)
                .build();
    }
} 
