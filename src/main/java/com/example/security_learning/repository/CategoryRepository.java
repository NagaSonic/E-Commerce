package com.example.security_learning.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security_learning.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}