package com.megamart.orderpaymentserver.service.interfaces;

import com.megamart.orderpaymentserver.dto.OrderRequest;
import com.megamart.orderpaymentserver.dto.OrderResponse;

/**
 * Interface for User Data Service
 * This defines methods for managing user addresses and payment methods
 */
public interface UserDataServiceInterface {
    
    /**
     * Get user's address by ID
     * @param userId - User ID
     * @param addressId - Address ID
     * @return Address details
     */
    OrderResponse.Address getAddress(String userId, Long addressId);
    
    /**
     * Create new address for user
     * @param userId - User ID
     * @param address - Address details
     * @return Created address with ID
     */
    OrderResponse.Address createAddress(String userId, OrderRequest.Address address);
    
    /**
     * Get user's payment method by ID
     * @param userId - User ID
     * @param paymentMethodId - Payment method ID
     * @return Payment method details
     */
    OrderResponse.PaymentMethod getPaymentMethod(String userId, Long paymentMethodId);
    
    /**
     * Create new payment method for user
     * @param userId - User ID
     * @param paymentMethod - Payment method details
     * @return Created payment method with ID
     */
    OrderResponse.PaymentMethod createPaymentMethod(String userId, OrderRequest.PaymentMethod paymentMethod);
} 
