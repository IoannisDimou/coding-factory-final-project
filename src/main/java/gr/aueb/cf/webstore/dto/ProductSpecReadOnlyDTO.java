package gr.aueb.cf.webstore.dto;

public record ProductSpecReadOnlyDTO(
    Long id,
    String name,
    String value,
    Long productId
) {}
