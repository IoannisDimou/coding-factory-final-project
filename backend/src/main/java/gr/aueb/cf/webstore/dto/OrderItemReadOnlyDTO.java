package gr.aueb.cf.webstore.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemReadOnlyDTO(
        Long productId,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal tax
) {}
