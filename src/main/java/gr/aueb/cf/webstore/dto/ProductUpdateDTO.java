package gr.aueb.cf.webstore.dto;

import java.math.BigDecimal;

public record ProductUpdateDTO(
        Long id,
        BigDecimal price,
        Integer stock,
        Boolean isActive,
        String description,
        String brand,
        String image,
        String name,
        String sku
) {}

