package com.example.security_learning.mapper;

import com.example.security_learning.dto.response.OrderItemResponse;
import com.example.security_learning.entity.OrderItem;

public final class OrderItemMapper {

    private OrderItemMapper() {
        // Utility class
    }

    public static OrderItemResponse toResponse(OrderItem item) {

        if (item == null) {
            return null;
        }

        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
        );
    }
}

