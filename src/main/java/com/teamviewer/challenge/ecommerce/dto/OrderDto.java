package com.teamviewer.challenge.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    @NotEmpty(message = "Order IDs list cannot be empty")
    @NotNull(message = "Order IDs list cannot be null")
    private List<Long> orderItemIds;
    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;
    @NotBlank(message = "Address cannot be blank")
    private String address;
}
