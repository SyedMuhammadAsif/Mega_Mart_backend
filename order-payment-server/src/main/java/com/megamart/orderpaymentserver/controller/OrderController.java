package com.megamart.orderpaymentserver.controller;

import com.megamart.orderpaymentserver.dto.OrderRequest;
import com.megamart.orderpaymentserver.dto.OrderResponse;
import com.megamart.orderpaymentserver.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {
    
    private final OrderService orderService;
    
    @Operation(summary = "Create new order", description = "Create a new order with items, address, and payment method")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        OrderResponse orderResponse = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }
    
    @Operation(summary = "Create order from cart", description = "Create order from user's cart items")
    @PostMapping("/from-cart/{userId}")
    public ResponseEntity<OrderResponse> createOrderFromCart(
            @PathVariable String userId,
            @Valid @RequestBody OrderFromCartRequest request) {
        log.info("Creating order from cart for user: {}", userId);
        OrderResponse orderResponse = orderService.createOrderFromCart(userId, request.getAddress(), request.getPaymentMethod());
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }
    
    public static class OrderFromCartRequest {
        private OrderRequest.Address address;
        private OrderRequest.PaymentMethod paymentMethod;
        
        public OrderRequest.Address getAddress() { return address; }
        public void setAddress(OrderRequest.Address address) { this.address = address; }
        public OrderRequest.PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(OrderRequest.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    
    @Operation(summary = "Get order by ID", description = "Retrieve order details by order ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(orderResponse);
    }
    
    @Operation(summary = "Get user orders", description = "Get all orders for a specific user with pagination")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponse> orders = orderService.getUserOrders(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(orders);
    }
    
    @Operation(summary = "Get all orders", description = "Get all orders with pagination (Admin only)")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderResponse> orders = orderService.getAllOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(orders);
    }
    
    @Operation(
        summary = "Update order status", 
        description = "Update order status following proper workflow: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED"
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Parameter(
                name = "status",
                description = "New order status - must follow proper workflow",
                in = ParameterIn.QUERY,
                required = true,
                schema = @Schema(
                    type = "string",
                    allowableValues = {"PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"},
                    example = "PROCESSING"
                )
            )
            @RequestParam String status,
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String notes) {
        OrderResponse orderResponse = orderService.updateOrderStatus(id, status, locationId, notes);
        return ResponseEntity.ok(orderResponse);
    }
    
    @Operation(summary = "Cancel order", description = "Cancel an existing order (auto-refund if payment completed)")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.cancelOrder(id);
        return ResponseEntity.ok(orderResponse);
    }
    
    @Operation(summary = "Delete order", description = "Delete an order (Admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order deleted successfully");
        response.put("orderId", id);
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get order tracking", description = "Get order tracking information")
    @GetMapping("/{id}/tracking")
    public ResponseEntity<Map<String, Object>> getOrderTracking(@PathVariable Long id) {
        log.info("Fetching tracking for order: {}", id);
        List<com.megamart.orderpaymentserver.entity.OrderTracking> trackingHistory = 
            orderService.getOrderTrackingHistory(id);
        
        Map<String, Object> tracking = new HashMap<>();
        tracking.put("orderId", id);
        tracking.put("trackingHistory", trackingHistory);
        
        return ResponseEntity.ok(tracking);
    }
    
    @Operation(summary = "Health check", description = "Check if the service is running")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Order-Payment Service");
        response.put("status", "UP");
        response.put("port", "9098");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Test orders endpoint", description = "Test if orders can be retrieved")
    @GetMapping("/debug/test")
    public ResponseEntity<Map<String, Object>> testOrders() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Order service is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Simple orders list", description = "Get orders without complex mapping")
    @GetMapping("/debug/simple")
    public ResponseEntity<Map<String, Object>> getSimpleOrders() {
        try {
            List<Map<String, Object>> orders = orderService.getSimpleOrdersList();
            Map<String, Object> response = new HashMap<>();
            response.put("content", orders);
            response.put("totalElements", orders.size());
            response.put("totalPages", 1);
            response.put("size", orders.size());
            response.put("number", 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in debug simple orders: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("content", new java.util.ArrayList<>());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    private String calculateEstimatedDelivery(String status) {
        return switch (status) {
            case "PENDING", "CONFIRMED" -> "5-7 business days";
            case "PROCESSING" -> "3-5 business days";
            case "SHIPPED" -> "1-2 business days";
            case "DELIVERED" -> "Delivered";
            case "CANCELLED" -> "N/A";
            default -> "Unknown";
        };
    }
    
    private String[] getAllowedNextStatuses(String currentStatus) {
        return switch (currentStatus) {
            case "PENDING" -> new String[]{"CONFIRMED", "CANCELLED"};
            case "CONFIRMED" -> new String[]{"PROCESSING", "CANCELLED"};
            case "PROCESSING" -> new String[]{"SHIPPED", "CANCELLED"};
            case "SHIPPED" -> new String[]{"DELIVERED"};
            case "DELIVERED", "CANCELLED" -> new String[]{}; // No further transitions
            default -> new String[]{};
        };
    }
} 
