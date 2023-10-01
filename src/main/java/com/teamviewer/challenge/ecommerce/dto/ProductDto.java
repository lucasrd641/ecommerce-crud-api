package com.teamviewer.challenge.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    @NotNull(message = "Product name cannot be null")
    @Size(min = 1, max = 255, message = "Product name should be between 1 and 255 characters")
    private String name;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Price cannot be negative or zero")
    @Digits(integer = 10, fraction = 2, message = "Price can have at most 10 integer digits and 2 fraction digits")
    private BigDecimal price;
}
