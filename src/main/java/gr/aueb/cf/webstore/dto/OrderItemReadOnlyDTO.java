package gr.aueb.cf.webstore.dto;

import java.math.BigDecimal;

public record OrderItemReadOnlyDTO(
        Long productId,
        String productName,
        int quantity,
        BigDecimal price,
        BigDecimal discount,
        BigDecimal tax
) {}
