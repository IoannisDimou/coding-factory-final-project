package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentConfirmationDTO(@NotBlank String paymentToken) {}
