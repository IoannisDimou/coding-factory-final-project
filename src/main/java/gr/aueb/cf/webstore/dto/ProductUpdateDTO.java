package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductUpdateDTO(

        @NotNull(message = "id field is required")
        Long id,

        @NotNull(message = "Price is required")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @PositiveOrZero(message = "Stock cannot be negative")
        Integer stock,

        @NotNull(message = "isActive field is required")
        Boolean isActive,

        String description,
        String brand,
        String image,
        String name,
        String sku
) {}

