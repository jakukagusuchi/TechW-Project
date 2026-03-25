package com.techw.ecommerce.service;

import com.techw.ecommerce.dto.CartDto;
import com.techw.ecommerce.dto.CartItemRequest;
import com.techw.ecommerce.dto.CheckoutRequest;
import com.techw.ecommerce.exception.BusinessRuleException;
import com.techw.ecommerce.exception.ResourceNotFoundException;
import com.techw.ecommerce.model.*;
import com.techw.ecommerce.repository.CartItemRepository;
import com.techw.ecommerce.repository.CartRepository;
import com.techw.ecommerce.repository.OrderRepository;
import com.techw.ecommerce.repository.ProductRepository;
import com.techw.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public CartDto getCartByUserEmail(String email) {
        Cart cart = getOrCreateCart(email);
        return mapToDto(cart);
    }

    @Transactional
    public CartDto addItemToCart(String email, CartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!product.getIsActive()) {
            throw new BusinessRuleException("Product is not active and cannot be added to cart");
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
        return mapToDto(cart);
    }

    @Transactional
    public CartDto updateItemQuantity(String email, UUID itemId, Integer quantity) {
        Cart cart = getOrCreateCart(email);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }

        cartRepository.save(cart);
        return mapToDto(cart);
    }

    @Transactional
    public CartDto removeItem(String email, UUID itemId) {
        Cart cart = getOrCreateCart(email);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cartRepository.save(cart);
        return mapToDto(cart);
    }

    @Transactional
    public void clearCart(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public Order checkout(String email, CheckoutRequest request) {
        Cart cart = getOrCreateCart(email);

        if (cart.getItems().isEmpty()) {
            throw new BusinessRuleException("Cannot checkout with an empty cart");
        }

        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String shippingAddress = request.getFullName() + ", " + request.getAddress() + ", " + request.getCity();
        if (request.getZipCode() != null && !request.getZipCode().isBlank()) {
            shippingAddress += " " + request.getZipCode();
        }

        Order.PaymentMethod paymentMethod;
        try {
            paymentMethod = Order.PaymentMethod.valueOf(request.getPaymentMethod());
        } catch (Exception e) {
            paymentMethod = Order.PaymentMethod.CASH_ON_DELIVERY;
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.PENDING)
                .paymentMethod(paymentMethod)
                .shippingAddress(shippingAddress)
                .billingAddress(shippingAddress)
                .build();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .productName(cartItem.getProduct().getName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getProduct().getPrice())
                    .build();
            order.getItems().add(orderItem);
        }

        order = orderRepository.save(order);

        // Clear the cart after checkout
        cart.getItems().clear();
        cartRepository.save(cart);

        return order;
    }

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).build();
            return cartRepository.save(newCart);
        });
    }

    private CartDto mapToDto(Cart cart) {
        return CartDto.builder()
                .id(cart.getId())
                .userEmail(cart.getUser().getEmail())
                .items(cart.getItems().stream().map(item -> CartDto.CartItemDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productPrice(item.getProduct().getPrice().doubleValue())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .quantity(item.getQuantity())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
