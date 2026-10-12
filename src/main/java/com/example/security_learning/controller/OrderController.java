package com.example.security_learning.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security_learning.dto.request.OrderRequest;
import com.example.security_learning.dto.response.OrderResponse;
import com.example.security_learning.service.OrderService;

import com.example.security_learning.dto.request.OrderStatusRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(
            Authentication authentication,
            @Valid @RequestBody OrderRequest request) {

        return orderService.createOrder(
                authentication.getName(),
                request
        );
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(
            Authentication authentication) {

        return orderService.getMyOrders(
                authentication.getName()
        );
    }

    @GetMapping("/{orderId}")
    public OrderResponse getMyOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        return orderService.getMyOrder(
                authentication.getName(),
                orderId
        );
    }
    
    @PutMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusRequest request) {

        return orderService.updateOrderStatus(orderId, request);
    }
}