package com.example.security_learning.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PaymentRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}