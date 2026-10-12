package com.example.security_learning.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security_learning.dto.request.CartItemRequest;
import com.example.security_learning.dto.response.CartItemResponse;
import com.example.security_learning.entity.Cart;
import com.example.security_learning.entity.CartItem;
import com.example.security_learning.entity.Inventory;
import com.example.security_learning.entity.Product;
import com.example.security_learning.entity.User;
import com.example.security_learning.exception.BusinessException;
import com.example.security_learning.mapper.CartMapper;
import com.example.security_learning.repository.CartItemRepository;
import com.example.security_learning.repository.CartRepository;
import com.example.security_learning.repository.InventoryRepository;
import com.example.security_learning.repository.ProductRepository;
import com.example.security_learning.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            InventoryRepository inventoryRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public CartItemResponse addItem(
            String username,
            CartItemRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new BusinessException("Product not found"));

        validateStock(product, request.getQuantity());

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        int existingQuantity = (cartItem == null)
                ? 0
                : cartItem.getQuantity();

        int totalQuantity = existingQuantity + request.getQuantity();

        validateStock(product, totalQuantity);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(totalQuantity);

        CartItem savedItem = cartItemRepository.save(cartItem);

        return CartMapper.toResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElse(null);

        // A user without a cart has an empty cart.
        if (cart == null) {
            return List.of();
        }

        return cartItemRepository.findByCart(cart)
                .stream()
                .map(CartMapper::toResponse)
                .toList();
    }

    @Transactional
    public CartItemResponse updateItem(
            String username,
            Long itemId,
            CartItemRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new BusinessException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new BusinessException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException(
                    "Cart item does not belong to this user");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new BusinessException("Product not found"));

        validateStock(product, request.getQuantity());

        cartItem.setProduct(product);
        cartItem.setQuantity(request.getQuantity());

        CartItem updatedItem = cartItemRepository.save(cartItem);

        return CartMapper.toResponse(updatedItem);
    }

    @Transactional
    public void removeItem(String username, Long itemId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new BusinessException("Cart not found"));

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new BusinessException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BusinessException(
                    "Cart item does not belong to this user");
        }

        cartItemRepository.delete(cartItem);
    }

    private void validateStock(
            Product product,
            Integer requestedQuantity) {

        Inventory inventory = inventoryRepository.findByProduct(product)
                .orElseThrow(() ->
                        new BusinessException(
                                "Inventory not found for product"));

        if (inventory.getQuantity() == null
                || inventory.getQuantity() < requestedQuantity) {

            throw new BusinessException(
                    "Insufficient stock available");
        }
    }
}
