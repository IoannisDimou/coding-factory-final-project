package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateDTO(

        @NotNull(message = "id field is required")
        Long id,

        @NotBlank(message = "Category name is required")
        String name,

        @NotNull(message = "isActive field is required")
        Boolean isActive
) {}
