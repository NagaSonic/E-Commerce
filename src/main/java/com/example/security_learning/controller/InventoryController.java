package com.example.security_learning.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security_learning.dto.request.InventoryRequest;
import com.example.security_learning.dto.response.InventoryResponse;
import com.example.security_learning.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public InventoryResponse createInventory(
            @Valid @RequestBody InventoryRequest request) {

        return inventoryService.createInventory(request);
    }

    @GetMapping
    public List<InventoryResponse> getAllInventory() {

        return inventoryService.getAllInventory();
    }

    @GetMapping("/{id}")
    public InventoryResponse getInventoryById(
            @PathVariable Long id) {

        return inventoryService.getInventoryById(id);
    }

    @PutMapping("/{id}")
    public InventoryResponse updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequest request) {

        return inventoryService.updateInventory(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteInventory(
            @PathVariable Long id) {

        inventoryService.deleteInventory(id);

        return "Inventory deleted successfully";
    }
}