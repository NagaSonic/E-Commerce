package com.example.security_learning.mapper;

import com.example.security_learning.dto.response.CategoryResponse;
import com.example.security_learning.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
        // Utility class
    }

    public static Category toEntity(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

    public static CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}

