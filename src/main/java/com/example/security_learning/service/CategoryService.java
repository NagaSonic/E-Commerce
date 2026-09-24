package com.example.security_learning.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.security_learning.dto.request.CategoryRequest;
import com.example.security_learning.dto.response.CategoryResponse;
import com.example.security_learning.entity.Category;
import com.example.security_learning.repository.CategoryRepository;

/**
 * Handles business operations related to product categories.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new product category.
     */
    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    /**
     * Returns all available product categories.
     */
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns a category by its ID.
     */
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        return toResponse(category);
    }

    /**
     * Updates an existing category.
     */
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        existingCategory.setName(request.getName());
        existingCategory.setDescription(request.getDescription());

        Category updatedCategory =
                categoryRepository.save(existingCategory);

        return toResponse(updatedCategory);
    }

    /**
     * Deletes an existing category.
     */
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }

    /**
     * Converts a Category entity into a CategoryResponse DTO.
     */
    private CategoryResponse toResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}