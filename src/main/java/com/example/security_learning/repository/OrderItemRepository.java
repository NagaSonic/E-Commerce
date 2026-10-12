package com.example.security_learning.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security_learning.entity.Order;
import com.example.security_learning.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}