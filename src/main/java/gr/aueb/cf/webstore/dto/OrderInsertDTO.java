package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderInsertDTO(
        @NotNull(message = "User id is required")
        Long userId,

        @NotNull(message = "Shipping address is required")
        AddressDTO shippingAddress,

        @NotEmpty(message = "At least one order item is required")
        List<OrderItemInsertDTO> items
) {}

