package com.example.security_learning.dto.response;

import java.math.BigDecimal;
import java.util.List;
import com.example.security_learning.entity.OrderStatus;

public class OrderResponse {

    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(Long id, BigDecimal totalAmount, OrderStatus status,
            List<OrderItemResponse> items) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}