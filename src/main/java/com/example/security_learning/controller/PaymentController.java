package com.example.security_learning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.security_learning.dto.request.PaymentRequest;
import com.example.security_learning.dto.response.PaymentResponse;
import com.example.security_learning.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> makePayment(
            Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse response =
                paymentService.makePayment(
                        authentication.getName(),
                        orderId,
                        request
                );

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(
            Authentication authentication,
            @PathVariable Long orderId) {

        PaymentResponse response =
                paymentService.getPayment(
                        authentication.getName(),
                        orderId
                );

        return ResponseEntity.ok(response);
    }
}