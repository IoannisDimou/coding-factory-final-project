package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductSpecInsertDTO(
       @NotBlank(message = "Name is required")
       String name,

       @NotBlank(message = "Value is required")
       String value
) {}
