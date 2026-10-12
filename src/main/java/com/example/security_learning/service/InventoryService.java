package com.example.security_learning.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.security_learning.dto.request.InventoryRequest;
import com.example.security_learning.dto.response.InventoryResponse;
import com.example.security_learning.entity.Inventory;
import com.example.security_learning.entity.Product;
import com.example.security_learning.repository.InventoryRepository;
import com.example.security_learning.repository.ProductRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public InventoryResponse createInventory(InventoryRequest request) {

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(request.getQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    public List<InventoryResponse> getAllInventory() {

        return inventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InventoryResponse getInventoryById(Long id) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        return toResponse(inventory);
    }

    public InventoryResponse updateInventory(
            Long id,
            InventoryRequest request) {

        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existingInventory.setProduct(product);
        existingInventory.setQuantity(request.getQuantity());

        Inventory updatedInventory = inventoryRepository.save(existingInventory);

        return toResponse(updatedInventory);
    }

    public void deleteInventory(Long id) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventoryRepository.delete(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getName(),
                inventory.getQuantity()
        );
    }
}