package com.example.security_learning.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Query;

import com.example.security_learning.entity.Inventory;
import com.example.security_learning.entity.Product;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	
Optional<Inventory> findByProduct(Product product);

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT i FROM Inventory i WHERE i.product = :product")
Optional<Inventory> findByProductForUpdate(@Param("product") Product product);


}
