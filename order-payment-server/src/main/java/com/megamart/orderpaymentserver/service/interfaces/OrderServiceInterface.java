package com.megamart.orderpaymentserver.service.interfaces;

import com.megamart.orderpaymentserver.dto.OrderRequest;
import com.megamart.orderpaymentserver.dto.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface for Order Service
 * This defines what methods the OrderService should have
 */
public interface OrderServiceInterface {
    
    /**
     * Create a new order
     * @param request - Order details
     * @return Created order with ID
     */
    OrderResponse createOrder(OrderRequest request);
    
    /**
     * Get order by ID
     * @param orderId - Order ID to find
     * @return Order details
     */
    OrderResponse getOrderById(Long orderId);
    
    /**
     * Get all orders for a specific user
     * @param userId - User ID
     * @param pageable - Page settings (page number, size)
     * @return List of user's orders
     */
    Page<OrderResponse> getUserOrders(String userId, Pageable pageable);
    
    /**
     * Get all orders (for admin)
     * @param pageable - Page settings
     * @return All orders
     */
    Page<OrderResponse> getAllOrders(Pageable pageable);
    
    /**
     * Update order status (for admin)
     * @param orderId - Order ID
     * @param status - New status
     * @return Updated order
     */
    OrderResponse updateOrderStatus(Long orderId, String status);
    
    /**
     * Cancel an order
     * @param orderId - Order ID to cancel
     * @return Cancelled order
     */
    OrderResponse cancelOrder(Long orderId);
    
    /**
     * Create order from user's cart
     * @param userId - User ID
     * @param address - Shipping address
     * @param paymentMethod - Payment method
     * @return Created order
     */
    OrderResponse createOrderFromCart(String userId, OrderRequest.Address address, OrderRequest.PaymentMethod paymentMethod);
} 
