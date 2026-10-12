package com.example.security_learning.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security_learning.dto.request.CartItemRequest;
import com.example.security_learning.dto.response.CartItemResponse;
import com.example.security_learning.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public CartItemResponse addItem(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {

        return cartService.addItem(
                authentication.getName(),
                request
        );
    }

    @GetMapping
    public List<CartItemResponse> getCart(
            Authentication authentication) {

        return cartService.getCart(
                authentication.getName()
        );
    }

    @PutMapping("/items/{itemId}")
    public CartItemResponse updateItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequest request) {

        return cartService.updateItem(
                authentication.getName(),
                itemId,
                request
        );
    }

    @DeleteMapping("/items/{itemId}")
    public String removeItem(
            Authentication authentication,
            @PathVariable Long itemId) {

        cartService.removeItem(
                authentication.getName(),
                itemId
        );

        return "Cart item removed successfully";
    }
}