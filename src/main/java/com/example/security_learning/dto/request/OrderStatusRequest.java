package com.example.security_learning.dto.request;

import com.example.security_learning.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;

public class OrderStatusRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}