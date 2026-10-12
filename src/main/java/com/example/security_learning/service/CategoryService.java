package com.example.security_learning.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.security_learning.dto.request.CategoryRequest;
import com.example.security_learning.dto.response.CategoryResponse;
import com.example.security_learning.entity.Category;
import com.example.security_learning.mapper.CategoryMapper;
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
        Category category = CategoryMapper.toEntity(
                request.getName(),
                request.getDescription()
        );

        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toResponse(savedCategory);
    }

    /**
     * Returns all available product categories.
     */
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    /**
     * Returns a category by its ID.
     */
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        return CategoryMapper.toResponse(category);
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

        return CategoryMapper.toResponse(updatedCategory);
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
}

