package com.example.security_learning.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security_learning.dto.request.OrderRequest;
import com.example.security_learning.dto.request.OrderStatusRequest;
import com.example.security_learning.dto.response.OrderItemResponse;
import com.example.security_learning.dto.response.OrderResponse;
import com.example.security_learning.entity.Cart;
import com.example.security_learning.entity.CartItem;
import com.example.security_learning.entity.Inventory;
import com.example.security_learning.entity.Order;
import com.example.security_learning.entity.OrderItem;
import com.example.security_learning.entity.OrderStatus;
import com.example.security_learning.entity.User;
import com.example.security_learning.exception.BusinessException;
import com.example.security_learning.mapper.OrderItemMapper;
import com.example.security_learning.mapper.OrderMapper;
import com.example.security_learning.repository.CartItemRepository;
import com.example.security_learning.repository.CartRepository;
import com.example.security_learning.repository.InventoryRepository;
import com.example.security_learning.repository.OrderItemRepository;
import com.example.security_learning.repository.OrderRepository;
import com.example.security_learning.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            InventoryRepository inventoryRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public OrderResponse createOrder(String username, OrderRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new BusinessException("Cart is empty");
        }

        // Process products in a consistent order to reduce deadlock risk.
        cartItems.sort(
                Comparator.comparing(item -> item.getProduct().getId()));

        // Lock and validate stock for every cart item before creating the order.
        for (CartItem cartItem : cartItems) {

            Inventory inventory = inventoryRepository
                    .findByProductForUpdate(cartItem.getProduct())
                    .orElseThrow(() -> new BusinessException(
                            "Inventory not found for product ID: "
                                    + cartItem.getProduct().getId()));

            if (inventory.getQuantity() == null
                    || inventory.getQuantity() < cartItem.getQuantity()) {

                throw new BusinessException(
                        "Insufficient stock for product: "
                                + cartItem.getProduct().getName());
            }
        }

        // Create the order after stock validation.
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(BigDecimal.ZERO);

        Order savedOrder = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Deduct stock and create order items within the same transaction.
        for (CartItem cartItem : cartItems) {

            Inventory inventory = inventoryRepository
                    .findByProductForUpdate(cartItem.getProduct())
                    .orElseThrow(() -> new BusinessException(
                            "Inventory not found for product ID: "
                                    + cartItem.getProduct().getId()));

            BigDecimal price = cartItem.getProduct().getPrice();
            BigDecimal subtotal = price.multiply(
                    BigDecimal.valueOf(cartItem.getQuantity()));

            inventory.setQuantity(
                    inventory.getQuantity() - cartItem.getQuantity());

            inventoryRepository.save(inventory);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(price);
            orderItem.setSubtotal(subtotal);

            orderItemRepository.save(orderItem);

            totalAmount = totalAmount.add(subtotal);
        }

        savedOrder.setTotalAmount(totalAmount);
        Order updatedOrder = orderRepository.save(savedOrder);

        // Clear the cart only after successful order processing.
        cartItemRepository.deleteAll(cartItems);

        List<OrderItem> savedItems =
                orderItemRepository.findByOrder(updatedOrder);

        return OrderMapper.toResponse(updatedOrder, savedItems);
    }

    public List<OrderResponse> getMyOrders(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return orderRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getMyOrder(String username, Long orderId) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Order does not belong to this user");
        }

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItem> items =
                orderItemRepository.findByOrder(order);

        return OrderMapper.toResponse(order, items);
    }

    private OrderItemResponse toItemResponse(OrderItem orderItem) {
        return OrderItemMapper.toResponse(orderItem);
    }

    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        order.setStatus(request.getStatus());

        Order updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }
}

