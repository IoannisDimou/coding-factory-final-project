package gr.aueb.cf.webstore.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductReadOnlyDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String sku,
        Boolean isActive,
        String brand,
        String image,
        CategoryReadOnlyDTO category,
        List<ProductSpecReadOnlyDTO> productSpecs
) {}
