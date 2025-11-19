package gr.aueb.cf.webstore.dto;

public record CategoryUpdateDTO(
        Long id,
        String name,
        Boolean isActive
) {}
