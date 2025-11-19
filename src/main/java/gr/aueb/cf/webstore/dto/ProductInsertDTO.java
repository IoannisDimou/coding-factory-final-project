package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductInsertDTO(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Price is required")
    BigDecimal price,

    @NotNull(message = "Category is required")
    Long categoryId,

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock cannot be negative")
    Integer stock,

    @NotBlank(message = "SKU is required")
    String sku,

    Boolean isActive,
    String brand,
    String image
) {}
