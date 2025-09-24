package com.megamart.orderpaymentserver.service;

import com.megamart.orderpaymentserver.dto.OrderRequest;
import com.megamart.orderpaymentserver.dto.OrderResponse;
import com.megamart.orderpaymentserver.exception.AddressNotFoundException;
import com.megamart.orderpaymentserver.exception.PaymentMethodNotFoundException;
import com.megamart.orderpaymentserver.exception.ValidationException;
import com.megamart.orderpaymentserver.service.interfaces.UserDataServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class MockUserDataService implements UserDataServiceInterface {
    
    private final Map<Long, OrderResponse.Address> addresses = new HashMap<>();
    private final Map<Long, OrderResponse.PaymentMethod> paymentMethods = new HashMap<>();
    private final AtomicLong addressIdCounter = new AtomicLong(1);
    private final AtomicLong paymentMethodIdCounter = new AtomicLong(1);
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String USER_SERVICE_URL = "http://localhost:9094/api";
    
    public MockUserDataService() {
        loadSampleData();
    }
    
    @Override
    public OrderResponse.Address getAddress(String userId, Long addressId) {
        try {
            // Try to fetch from user-admin service first
            Map<String, Object> response = restTemplate.getForObject(
                USER_SERVICE_URL + "/addresses/" + addressId, 
                Map.class
            );
            
            if (response != null) {
                return OrderResponse.Address.builder()
                        .id(Long.valueOf(response.get("id").toString()))
                        .fullName((String) response.get("fullName"))
                        .addressLine1((String) response.get("addressLine1"))
                        .addressLine2((String) response.get("addressLine2"))
                        .city((String) response.get("city"))
                        .state((String) response.get("state"))
                        .postalCode((String) response.get("postalCode"))
                        .country((String) response.get("country"))
                        .phone((String) response.get("phone"))
                        .build();
            }
        } catch (Exception e) {
            log.error("Failed to fetch address from user service: {}", e.getMessage());
        }
        
        // Fallback to local storage
        OrderResponse.Address address = addresses.get(addressId);
        if (address == null || !address.getId().equals(addressId)) {
            throw new AddressNotFoundException(Integer.valueOf(userId), addressId);
        }
        return address;
    }
    
    @Override
    public OrderResponse.Address createAddress(String userId, OrderRequest.Address address) {
        validateAddress(address);
        
        // Don't create address in user-admin service during order creation
        // Just return a temporary address object for order processing
        Long newId = addressIdCounter.getAndIncrement();
        OrderResponse.Address newAddress = OrderResponse.Address.builder()
                .id(newId)
                .fullName(address.getFullName())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .phone(address.getPhone())
                .isDefault(address.getIsDefault())
                .build();
        
        addresses.put(newId, newAddress);
        log.info("Created temporary address for order with ID: {}", newId);
        return newAddress;
    }
    
    @Override
    public OrderResponse.PaymentMethod getPaymentMethod(String userId, Long paymentMethodId) {
        OrderResponse.PaymentMethod paymentMethod = paymentMethods.get(paymentMethodId);
        if (paymentMethod == null || !paymentMethod.getId().equals(paymentMethodId)) {
            throw new PaymentMethodNotFoundException(Integer.valueOf(userId), paymentMethodId);
        }
        return paymentMethod;
    }
    
    @Override
    public OrderResponse.PaymentMethod createPaymentMethod(String userId, OrderRequest.PaymentMethod paymentMethod) {
        validatePaymentMethod(paymentMethod);
        
        Long newId = paymentMethodIdCounter.getAndIncrement();
        OrderResponse.PaymentMethod newPaymentMethod = OrderResponse.PaymentMethod.builder()
                .id(newId)
                .type(paymentMethod.getType())
                .cardNumber(maskCardNumber(paymentMethod.getCardNumber()))
                .cardholderName(paymentMethod.getCardholderName())
                .expiryMonth(paymentMethod.getExpiryMonth())
                .expiryYear(paymentMethod.getExpiryYear())
                .upiId(paymentMethod.getUpiId())
                .isDefault(paymentMethod.getIsDefault())
                .build();
        
        paymentMethods.put(newId, newPaymentMethod);
        log.info("Created payment method with ID: {}", newId);
        return newPaymentMethod;
    }
    
    private void validateAddress(OrderRequest.Address address) {
        if (address.getPhone() == null || !address.getPhone().matches("^[0-9]{10}$")) {
            throw new ValidationException("phone", "Phone number must be exactly 10 digits");
        }
    }
    
    private void validatePaymentMethod(OrderRequest.PaymentMethod paymentMethod) {
        if ("CARD".equals(paymentMethod.getType())) {
            if (paymentMethod.getCardNumber() == null || !paymentMethod.getCardNumber().matches("^[0-9]{16}$")) {
                throw new ValidationException("cardNumber", "Card number must be exactly 16 digits");
            }
            if (paymentMethod.getCvv() == null || !paymentMethod.getCvv().matches("^[0-9]{3}$")) {
                throw new ValidationException("cvv", "CVV must be exactly 3 digits");
            }
        }
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.contains("*")) {
            return cardNumber;
        }
        if (cardNumber.length() >= 4) {
            String lastFour = cardNumber.substring(cardNumber.length() - 4);
            return "****-****-****-" + lastFour;
        }
        return cardNumber;
    }
    
    private void loadSampleData() {
        OrderResponse.Address address1 = OrderResponse.Address.builder()
                .id(1L)
                .fullName("John Doe")
                .addressLine1("123 Main Street")
                .city("New York")
                .state("NY")
                .postalCode("10001")
                .country("USA")
                .phone("1234567890")
                .isDefault(true)
                .build();
        addresses.put(1L, address1);
        
        OrderResponse.PaymentMethod paymentMethod1 = OrderResponse.PaymentMethod.builder()
                .id(1L)
                .type("CARD")
                .cardNumber("****-****-****-1234")
                .cardholderName("John Doe")
                .expiryMonth("12")
                .expiryYear("2025")
                .isDefault(true)
                .build();
        paymentMethods.put(1L, paymentMethod1);
        
        addressIdCounter.set(2L);
        paymentMethodIdCounter.set(2L);
        
        log.info("Loaded sample data");
    }
} 
