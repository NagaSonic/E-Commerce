package com.example.security_learning.mapper;

import java.math.BigDecimal;

import com.example.security_learning.dto.response.CartItemResponse;
import com.example.security_learning.entity.CartItem;

public final class CartMapper {

    private CartMapper() {
        // Utility class
    }

    public static CartItemResponse toResponse(CartItem cartItem) {

        if (cartItem == null) {
            return null;
        }

        BigDecimal price = cartItem.getProduct().getPrice();
        Integer quantity = cartItem.getQuantity();

        BigDecimal subtotal = price.multiply(
                BigDecimal.valueOf(quantity)
        );

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                price,
                quantity,
                subtotal
        );
    }
}
