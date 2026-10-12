

package com.example.security_learning.mapper;



import com.example.security_learning.dto.response.ProductResponse;
import com.example.security_learning.entity.Product;

public final class ProductMapper {

    private ProductMapper() {
        // Utility class
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        Long categoryId = null;
        String categoryName = null;

        if (product.getCategory() != null) {
            categoryId = product.getCategory().getId();
            categoryName = product.getCategory().getName();
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                categoryId,
                categoryName
        );
    }
}