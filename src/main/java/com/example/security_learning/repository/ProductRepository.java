package com.example.security_learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security_learning.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}