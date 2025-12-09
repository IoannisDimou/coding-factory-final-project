package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentRequestDTO (
        @NotNull
        Long orderId,

        @NotNull
        PaymentMethod method,

        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
        String cardNumber
) {}
