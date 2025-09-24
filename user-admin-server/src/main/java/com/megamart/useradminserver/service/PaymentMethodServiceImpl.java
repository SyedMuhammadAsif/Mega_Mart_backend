package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.PaymentMethodDto;
import com.megamart.useradminserver.entity.UserPaymentMethod;
import com.megamart.useradminserver.exception.ResourceNotFoundException;
import com.megamart.useradminserver.repository.UserPaymentMethodRepository;
import com.megamart.useradminserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {
    
    private final UserPaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserPaymentMethod> getUserPaymentMethods(String userId) {
        validateUserExists(userId);
        return paymentMethodRepository.findByUserId(userId);
    }

    @Override
    public UserPaymentMethod addPaymentMethod(String userId, PaymentMethodDto paymentMethodDto) {
        validateUserExists(userId);
        
        UserPaymentMethod paymentMethod = new UserPaymentMethod();
        paymentMethod.setUserId(userId);
        mapDtoToEntity(paymentMethodDto, paymentMethod);
        
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public UserPaymentMethod updatePaymentMethod(String userId, Long methodId, PaymentMethodDto paymentMethodDto) {
        validateUserExists(userId);
        
        UserPaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + methodId));
        
        if (!paymentMethod.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Payment method not found for user: " + userId);
        }
        
        mapDtoToEntity(paymentMethodDto, paymentMethod);
        return paymentMethodRepository.save(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(String userId, Long methodId) {
        validateUserExists(userId);
        
        UserPaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + methodId));
        
        if (!paymentMethod.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Payment method not found for user: " + userId);
        }
        
        paymentMethodRepository.delete(paymentMethod);
    }

    private void validateUserExists(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException("User not found with userId: " + userId);
        }
    }

    private void mapDtoToEntity(PaymentMethodDto dto, UserPaymentMethod entity) {
        entity.setType(dto.getType());
        
        if (dto.getType() == UserPaymentMethod.PaymentType.card) {
            // Store masked card number (only last 4 digits visible)
            String cardNumber = dto.getCardNumber().replaceAll("\\s", "");
            entity.setCardNumber(maskCardNumber(cardNumber));
            entity.setCardholderName(dto.getCardholderName());
            entity.setExpiryMonth(dto.getExpiryMonth());
            entity.setExpiryYear(dto.getExpiryYear());
            entity.setUpiId(null);
        } else if (dto.getType() == UserPaymentMethod.PaymentType.upi) {
            entity.setUpiId(dto.getUpiId());
            entity.setCardNumber(null);
            entity.setCardholderName(null);
            entity.setExpiryMonth(null);
            entity.setExpiryYear(null);
        }
        
        entity.setIsDefault(dto.getIsDefault());
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}