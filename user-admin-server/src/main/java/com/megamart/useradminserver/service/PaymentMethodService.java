package com.megamart.useradminserver.service;

import com.megamart.useradminserver.dto.PaymentMethodDto;
import com.megamart.useradminserver.entity.UserPaymentMethod;

import java.util.List;

public interface PaymentMethodService {
    List<UserPaymentMethod> getUserPaymentMethods(String userId);
    UserPaymentMethod addPaymentMethod(String userId, PaymentMethodDto paymentMethodDto);
    UserPaymentMethod updatePaymentMethod(String userId, Long methodId, PaymentMethodDto paymentMethodDto);
    void deletePaymentMethod(String userId, Long methodId);
}