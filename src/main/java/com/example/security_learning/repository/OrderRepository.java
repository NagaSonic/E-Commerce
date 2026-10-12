package com.example.security_learning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.example.security_learning.entity.Order;
import com.example.security_learning.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    Optional<Order> findByIdForUpdate(@Param("orderId") Long orderId);
}

