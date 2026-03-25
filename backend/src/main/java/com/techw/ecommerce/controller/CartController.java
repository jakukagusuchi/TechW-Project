package com.techw.ecommerce.controller;

import com.techw.ecommerce.dto.ApiResponse;
import com.techw.ecommerce.dto.CartDto;
import com.techw.ecommerce.dto.CartItemRequest;
import com.techw.ecommerce.dto.CheckoutRequest;
import com.techw.ecommerce.dto.OrderDto;
import com.techw.ecommerce.model.Order;
import com.techw.ecommerce.service.CartService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<CartDto>> getCart(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartByUserEmail(authentication.getName())));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItemToCart(Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success(cartService.addItemToCart(authentication.getName(), request)));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> updateItemQuantity(Authentication authentication,
            @PathVariable UUID itemId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(
                ApiResponse.success(cartService.updateItemQuantity(authentication.getName(), itemId, quantity)));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeItemFromCart(Authentication authentication,
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.removeItem(authentication.getName(), itemId)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderDto>> checkout(Authentication authentication,
            @Valid @RequestBody CheckoutRequest request) {
        Order order = cartService.checkout(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.created(OrderDto.fromEntity(order)));
    }
}
