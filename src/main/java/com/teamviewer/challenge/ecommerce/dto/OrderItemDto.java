package com.teamviewer.challenge.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemDto {
    @NotNull(message = "Product ID cannot be null")
    @Positive(message = "Quantity must be greater than zero")
    private Long productId;

    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}
