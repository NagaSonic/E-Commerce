package com.example.security_learning.mapper;

import com.example.security_learning.dto.response.PaymentResponse;
import com.example.security_learning.entity.Payment;

public final class PaymentMapper {

    private PaymentMapper() {
        // Utility class
    }

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getStatus()
        );
    }
}

