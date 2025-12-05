package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateDTO(

        @NotNull(message = "id field is required")
        Long id,

        @NotNull(message = "Order status is required")
        OrderStatus status
) {}

