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

import jakarta.validation.Valid;

import com.example.security_learning.dto.request.CategoryRequest;
import com.example.security_learning.dto.response.CategoryResponse;
import com.example.security_learning.service.CategoryService;

/**
 * Provides REST endpoints for managing product categories.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates a new product category.
     *
     * @param request category details
     * @return the created category
     */
    @PostMapping
    public CategoryResponse createCategory(
            @Valid @RequestBody CategoryRequest request) {

        return categoryService.createCategory(request);
    }

    /**
     * Returns all available product categories.
     *
     * @return list of categories
     */
    @GetMapping
    public List<CategoryResponse> getAllCategories() {

        return categoryService.getAllCategories();
    }

    /**
     * Returns a category by its ID.
     *
     * @param id category ID
     * @return category matching the given ID
     */
    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(
            @PathVariable Long id) {

        return categoryService.getCategoryById(id);
    }

    /**
     * Updates an existing category.
     *
     * @param id category ID
     * @param request updated category details
     * @return the updated category
     */
    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        return categoryService.updateCategory(id, request);
    }

    /**
     * Deletes an existing category.
     *
     * @param id category ID
     * @return deletion confirmation message
     */
    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return "Category deleted successfully";
    }
}