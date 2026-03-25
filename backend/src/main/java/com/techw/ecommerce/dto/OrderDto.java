package com.techw.ecommerce.dto;

import com.techw.ecommerce.model.Order;
import com.techw.ecommerce.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private String customerEmail;
    private String customerName;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String shippingAddress;
    private String billingAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private UUID id;
        private String productName;
        private BigDecimal unitPrice;
        private Integer quantity;
    }

    public static OrderDto fromEntity(Order order) {
        String email = "";
        String name = "";
        try {
            if (order.getUser() != null) {
                email = order.getUser().getEmail() != null ? order.getUser().getEmail() : "";
                String fn = order.getUser().getFirstName() != null ? order.getUser().getFirstName() : "";
                String ln = order.getUser().getLastName() != null ? order.getUser().getLastName() : "";
                name = (fn + " " + ln).trim();
            }
        } catch (Exception e) {
            // Lazy loading issue fallback
        }

        return OrderDto.builder()
                .id(order.getId())
                .customerEmail(email)
                .customerName(name)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus() != null ? order.getStatus().name() : "PENDING")
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : "N/A")
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems() != null ? order.getItems().stream().map(i -> OrderItemDto.builder()
                        .id(i.getId())
                        .productName(i.getProductName())
                        .unitPrice(i.getUnitPrice())
                        .quantity(i.getQuantity())
                        .build()).collect(Collectors.toList()) : List.of())
                .build();
    }
}
