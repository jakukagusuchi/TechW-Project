package com.techw.ecommerce.controller;

import com.techw.ecommerce.model.Order;
import com.techw.ecommerce.repository.OrderRepository;
import com.techw.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .map(user -> {
                    List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);
                    var dtos = orders.stream().map(o -> java.util.Map.of(
                            "id", o.getId(),
                            "createdAt", o.getCreatedAt(),
                            "status", o.getStatus(),
                            "totalAmount", o.getTotalAmount(),
                            "items", o.getItems().stream().map(i -> java.util.Map.of(
                                    "id", i.getId(),
                                    "productName", i.getProductName(),
                                    "quantity", i.getQuantity(),
                                    "unitPrice", i.getUnitPrice()
                            )).toList()
                    )).toList();
                    return ResponseEntity.ok(dtos);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable UUID userId) {
        // In a real app, you'd check if the requesting user IS the userId or is an
        // ADMIN
        return ResponseEntity.ok(orderRepository.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable UUID id, @RequestParam Order.OrderStatus status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
