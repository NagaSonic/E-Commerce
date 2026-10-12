package com.example.security_learning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security_learning.entity.Cart;
import com.example.security_learning.entity.CartItem;
import com.example.security_learning.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}