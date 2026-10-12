
package com.example.security_learning.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security_learning.dto.request.PaymentRequest;
import com.example.security_learning.dto.response.PaymentResponse;
import com.example.security_learning.entity.Order;
import com.example.security_learning.entity.OrderStatus;
import com.example.security_learning.entity.Payment;
import com.example.security_learning.entity.PaymentStatus;
import com.example.security_learning.entity.User;
import com.example.security_learning.mapper.PaymentMapper;
import com.example.security_learning.repository.OrderRepository;
import com.example.security_learning.repository.PaymentRepository;
import com.example.security_learning.repository.UserRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PaymentResponse makePayment(
            String username,
            Long orderId,
            PaymentRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lock the order row to serialize payment requests for this order.
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Order does not belong to this user");
        }

        if (!"UPI".equalsIgnoreCase(request.getPaymentMethod())
                && !"CARD".equalsIgnoreCase(request.getPaymentMethod())
                && !"FAIL".equalsIgnoreCase(request.getPaymentMethod())) {
            throw new IllegalArgumentException("Unsupported payment method");
        }

        // Return the original payment for repeated requests.
        var existingPayment = paymentRepository.findByOrder(order);

        if (existingPayment.isPresent()) {
            Payment previousPayment = existingPayment.get();

            if (previousPayment.getStatus() != PaymentStatus.FAILED) {
                return PaymentMapper.toResponse(previousPayment);
            }

            paymentRepository.delete(previousPayment);
            paymentRepository.flush();
        }

        if (order.getStatus() != OrderStatus.PLACED) {
            throw new RuntimeException(
                    "Payment is allowed only for PLACED orders");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId("TXN-" + UUID.randomUUID());

        payment.setStatus(
                "FAIL".equalsIgnoreCase(request.getPaymentMethod())
                        ? PaymentStatus.FAILED
                        : PaymentStatus.SUCCESS
        );

        Payment savedPayment = paymentRepository.save(payment);

        if (savedPayment.getStatus() == PaymentStatus.SUCCESS) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
        }

        return PaymentMapper.toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String username, Long orderId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Order does not belong to this user");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return PaymentMapper.toResponse(payment);
    }
}

