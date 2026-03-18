package com.techw.ecommerce.controller;

import com.techw.ecommerce.dto.CartDto;
import com.techw.ecommerce.dto.CartItemRequest;
import com.techw.ecommerce.model.Order;
import com.techw.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCartByUserEmail(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(Authentication authentication, @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(authentication.getName(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDto> updateItemQuantity(Authentication authentication, @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(authentication.getName(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDto> removeItemFromCart(Authentication authentication, @PathVariable UUID itemId) {
        return ResponseEntity.ok(cartService.removeItem(authentication.getName(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(Authentication authentication) {
        return ResponseEntity.ok(cartService.checkout(authentication.getName()));
    }
}
