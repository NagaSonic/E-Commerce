
package com.example.security_learning.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.security_learning.dto.request.ProductRequest;
import com.example.security_learning.dto.response.ProductResponse;
import com.example.security_learning.entity.Category;
import com.example.security_learning.entity.Product;
import com.example.security_learning.mapper.ProductMapper;
import com.example.security_learning.repository.CategoryRepository;
import com.example.security_learning.repository.ProductRepository;

/**
 * Handles business operations related to products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new product.
     */
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);
    }

    /**
     * Returns all available products.
     */
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    /**
     * Returns a product by its ID.
     */
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    /**
     * Updates an existing product.
     */
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(category);

        Product updatedProduct =
                productRepository.save(existingProduct);

        return ProductMapper.toResponse(updatedProduct);
    }

    /**
     * Deletes an existing product.
     */
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        productRepository.delete(product);
    }
}

