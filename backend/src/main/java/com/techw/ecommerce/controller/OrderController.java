package com.techw.ecommerce.controller;

import com.techw.ecommerce.dto.ApiResponse;
import com.techw.ecommerce.dto.OrderDto;
import com.techw.ecommerce.exception.BusinessRuleException;
import com.techw.ecommerce.exception.ResourceNotFoundException;
import com.techw.ecommerce.model.Order;
import com.techw.ecommerce.model.User;
import com.techw.ecommerce.repository.OrderRepository;
import com.techw.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrders() {
        List<OrderDto> dtos = orderRepository.findAll().stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/my-orders")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));
        List<OrderDto> dtos = orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable UUID id, Authentication authentication) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        boolean isOwner = order.getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to view this order");
        }
        return ResponseEntity.ok(ApiResponse.success(OrderDto.fromEntity(order)));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(@PathVariable UUID id,
            @RequestParam Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        order.setStatus(status);
        return ResponseEntity.ok(ApiResponse.success(OrderDto.fromEntity(orderRepository.save(order))));
    }

    @PutMapping("/{id}/cancel")
    @Transactional
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(@PathVariable UUID id, Authentication authentication) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to cancel this order");
        }
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be cancelled");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        return ResponseEntity.ok(ApiResponse.success(OrderDto.fromEntity(orderRepository.save(order))));
    }

    @PutMapping("/{id}/update")
    @Transactional
    public ResponseEntity<ApiResponse<OrderDto>> updateOrder(@PathVariable UUID id,
            @RequestBody Map<String, String> updates, Authentication authentication) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", authentication.getName()));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to update this order");
        }
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING orders can be updated");
        }
        if (updates.containsKey("shippingAddress") && updates.get("shippingAddress") != null) {
            order.setShippingAddress(updates.get("shippingAddress"));
            order.setBillingAddress(updates.get("shippingAddress"));
        }
        if (updates.containsKey("paymentMethod") && updates.get("paymentMethod") != null) {
            try {
                order.setPaymentMethod(Order.PaymentMethod.valueOf(updates.get("paymentMethod")));
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Invalid payment method: " + updates.get("paymentMethod"));
            }
        }
        return ResponseEntity.ok(ApiResponse.success(OrderDto.fromEntity(orderRepository.save(order))));
    }
}
