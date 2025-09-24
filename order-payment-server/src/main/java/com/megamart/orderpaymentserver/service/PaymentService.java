package com.megamart.orderpaymentserver.service;

import com.megamart.orderpaymentserver.dto.PaymentRequest;
import com.megamart.orderpaymentserver.dto.OrderResponse;
import com.megamart.orderpaymentserver.entity.Order;
import com.megamart.orderpaymentserver.entity.Payment;
import com.megamart.orderpaymentserver.exception.InvalidOrderStatusException;
import com.megamart.orderpaymentserver.exception.InvalidRequestException;
import com.megamart.orderpaymentserver.exception.OrderNotFoundException;
import com.megamart.orderpaymentserver.exception.PaymentNotFoundException;
import com.megamart.orderpaymentserver.exception.PaymentProcessingException;
import com.megamart.orderpaymentserver.repository.OrderRepository;
import com.megamart.orderpaymentserver.repository.PaymentRepository;
import com.megamart.orderpaymentserver.service.interfaces.PaymentServiceInterface;
import com.megamart.orderpaymentserver.service.interfaces.UserDataServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService implements PaymentServiceInterface {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserDataServiceInterface userDataService;
    private final MappingService mappingService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public OrderResponse.Payment processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        
        checkIfPaymentRequestIsValid(request);
        
        Order order = findOrderById(request.getOrderId());
        checkIfCanProcessPayment(order);
        
        OrderResponse.PaymentMethod paymentMethod = getOrCreatePaymentMethod(request, order.getUserId());
        
        Payment payment = createOrUpdatePayment(order, request, paymentMethod.getId());
        boolean paymentSuccess = processPaymentWithGateway(paymentMethod);
        updatePaymentStatus(payment, order, paymentSuccess);
        
        Payment savedPayment = paymentRepository.save(payment);
        orderRepository.save(order);
        
        return mappingService.mapToPaymentResponse(savedPayment, paymentMethod);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse.Payment getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
        
        OrderResponse.PaymentMethod paymentMethod = getPaymentMethodDetails(payment);
        return mappingService.mapToPaymentResponse(payment, paymentMethod);
    }
    
    @Override
    @Transactional(readOnly = true)
    public OrderResponse.Payment getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException(transactionId, true));
        
        OrderResponse.PaymentMethod paymentMethod = getPaymentMethodDetails(payment);
        return mappingService.mapToPaymentResponse(payment, paymentMethod);
    }
    
    // Helper methods
    private void checkIfPaymentRequestIsValid(PaymentRequest request) {
        if (request.getPaymentMethodId() == null && request.getNewPaymentMethod() == null) {
            throw new InvalidRequestException("paymentMethod", "Either paymentMethodId or newPaymentMethod must be provided");
        }
    }
    
    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
    
    private void checkIfCanProcessPayment(Order order) {
        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException("Cannot process payment for cancelled order");
        }
        if (order.getOrderStatus() == Order.OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException("Cannot process payment for delivered order");
        }
    }
    
    private OrderResponse.PaymentMethod getOrCreatePaymentMethod(PaymentRequest request, String userId) {
        if (request.getPaymentMethodId() != null) {
            return getPaymentMethodDetails(userId, request.getPaymentMethodId());
        } else {
            // Hash CVV before storing
            if (request.getNewPaymentMethod() != null && request.getNewPaymentMethod().getCvv() != null) {
                String hashedCvv = hashCvv(request.getNewPaymentMethod().getCvv());
                request.getNewPaymentMethod().setCvv(hashedCvv);
            }
            return userDataService.createPaymentMethod(userId, request.getNewPaymentMethod());
        }
    }
    
    private String hashCvv(String cvv) {
        return passwordEncoder.encode(cvv);
    }
    
    private boolean verifyCvv(String rawCvv, String hashedCvv) {
        return passwordEncoder.matches(rawCvv, hashedCvv);
    }
    
    private Payment createOrUpdatePayment(Order order, PaymentRequest request, Long paymentMethodId) {
        Payment payment = order.getPayment();
        
        if (payment == null) {
            payment = Payment.builder()
                    .order(order)
                    .userId(order.getUserId())
                    .amount(order.getTotal())
                    .paymentMethodId(paymentMethodId)
                    .paymentStatus(Payment.PaymentStatus.PROCESSING)
                    .paymentDate(LocalDateTime.now())
                    .transactionId(generateTransactionId())
                    .build();
        } else {
            payment.setAmount(order.getTotal());
            payment.setPaymentMethodId(paymentMethodId);
            payment.setPaymentStatus(Payment.PaymentStatus.PROCESSING);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setTransactionId(generateTransactionId());
        }
        
        return payment;
    }
    
    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private boolean processPaymentWithGateway(OrderResponse.PaymentMethod paymentMethod) {
        try {
            Thread.sleep(1000);
            return switch (paymentMethod.getType()) {
                case "COD" -> true;
                case "UPI" -> Math.random() > 0.1;
                case "CARD" -> Math.random() > 0.05;
                default -> true;
            };
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private void updatePaymentStatus(Payment payment, Order order, boolean paymentSuccess) {
        if (paymentSuccess) {
            payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
            order.setPaymentStatus(Order.PaymentStatus.COMPLETED);
            order.setOrderStatus(Order.OrderStatus.CONFIRMED);
        } else {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            order.setPaymentStatus(Order.PaymentStatus.FAILED);
            throw new PaymentProcessingException(order.getId(), "Payment gateway declined");
        }
    }
    
    private OrderResponse.PaymentMethod getPaymentMethodDetails(String userId, Long paymentMethodId) {
        try {
            return userDataService.getPaymentMethod(userId, paymentMethodId);
        } catch (Exception e) {
            log.error("Error getting payment method: {}", e.getMessage());
            throw new PaymentProcessingException("Failed to get payment method: " + e.getMessage());
        }
    }
    
    private OrderResponse.PaymentMethod getPaymentMethodDetails(Payment payment) {
        if (payment.getPaymentMethodId() != null) {
            return getPaymentMethodDetails(payment.getUserId(), payment.getPaymentMethodId());
        }
        return null;
    }
} 
