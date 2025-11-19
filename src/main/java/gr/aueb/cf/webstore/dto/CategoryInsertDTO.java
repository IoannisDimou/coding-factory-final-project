package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryInsertDTO(

        @NotBlank(message = "Category name is required")
        String name,

        Boolean isActive
) {}

