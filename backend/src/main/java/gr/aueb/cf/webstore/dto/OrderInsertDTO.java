package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderInsertDTO(
        @NotBlank(message = "User UUID is required")
        String userUuid,

        @NotNull(message = "Shipping address is required")
        AddressDTO shippingAddress,

        @NotEmpty(message = "At least one order item is required")
        List<OrderItemInsertDTO> items
) {}

