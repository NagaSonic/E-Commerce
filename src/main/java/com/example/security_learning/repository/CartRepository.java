package com.example.security_learning.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security_learning.entity.Cart;
import com.example.security_learning.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}