package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.PaymentMethod;
import gr.aueb.cf.webstore.core.enums.PaymentStatus;
import java.math.BigDecimal;

public record PaymentReadOnlyDTO(
        Long id,
        String transactionId,
        String paymentToken,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String cardBrand,
        String cardLastFourDigits,
        Long orderId
) {}
