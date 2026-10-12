package com.example.security_learning.dto.response;

import java.math.BigDecimal;

import com.example.security_learning.entity.PaymentStatus;

public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionId;
    private PaymentStatus status;

    public PaymentResponse(Long id,
                           Long orderId,
                           BigDecimal amount,
                           String paymentMethod,
                           String transactionId,
                           PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}