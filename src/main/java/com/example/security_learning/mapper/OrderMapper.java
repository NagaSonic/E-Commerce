package com.example.security_learning.mapper;

import java.util.List;

import com.example.security_learning.dto.response.OrderItemResponse;
import com.example.security_learning.dto.response.OrderResponse;
import com.example.security_learning.entity.Order;
import com.example.security_learning.entity.OrderItem;

public final class OrderMapper {

    private OrderMapper() {
        // Utility class
    }

    public static OrderResponse toResponse(
            Order order,
            List<OrderItem> orderItems) {

        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses =
                orderItems == null
                        ? List.of()
                        : orderItems.stream()
                                .map(OrderItemMapper::toResponse)
                                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                itemResponses
        );
    }
}

