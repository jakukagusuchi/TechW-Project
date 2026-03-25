package com.techw.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    @jakarta.validation.constraints.NotBlank(message = "Full name is required")
    private String fullName;

    @jakarta.validation.constraints.NotBlank(message = "Address is required")
    private String address;

    @jakarta.validation.constraints.NotBlank(message = "City is required")
    private String city;

    private String zipCode;

    @jakarta.validation.constraints.NotBlank(message = "Payment method is required")
    private String paymentMethod; // BANK_TRANSFER, QR_CODE, CASH_ON_DELIVERY
}
