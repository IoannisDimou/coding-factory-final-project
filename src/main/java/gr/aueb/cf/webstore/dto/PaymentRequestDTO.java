package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDTO (@NotNull Long orderId, @NotNull PaymentMethod method) {}
