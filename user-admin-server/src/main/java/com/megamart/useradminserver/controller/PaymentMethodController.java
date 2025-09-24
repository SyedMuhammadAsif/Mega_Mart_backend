package com.megamart.useradminserver.controller;

import com.megamart.useradminserver.dto.MessageDto;
import com.megamart.useradminserver.dto.PaymentMethodDto;
import com.megamart.useradminserver.entity.UserPaymentMethod;
import com.megamart.useradminserver.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/payment-methods")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<List<UserPaymentMethod>> getUserPaymentMethods(@PathVariable String userId) {
        return ResponseEntity.ok(paymentMethodService.getUserPaymentMethods(userId));
    }

    @PostMapping
    public ResponseEntity<UserPaymentMethod> addPaymentMethod(@PathVariable String userId, @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
        UserPaymentMethod paymentMethod = paymentMethodService.addPaymentMethod(userId, paymentMethodDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMethod);
    }

    @PutMapping("/{methodId}")
    public ResponseEntity<UserPaymentMethod> updatePaymentMethod(@PathVariable String userId, @PathVariable Long methodId, @Valid @RequestBody PaymentMethodDto paymentMethodDto) {
        UserPaymentMethod paymentMethod = paymentMethodService.updatePaymentMethod(userId, methodId, paymentMethodDto);
        return ResponseEntity.ok(paymentMethod);
    }

    @DeleteMapping("/{methodId}")
    public ResponseEntity<MessageDto> deletePaymentMethod(@PathVariable String userId, @PathVariable Long methodId) {
        paymentMethodService.deletePaymentMethod(userId, methodId);
        return ResponseEntity.ok(new MessageDto("Payment method deleted successfully"));
    }
}