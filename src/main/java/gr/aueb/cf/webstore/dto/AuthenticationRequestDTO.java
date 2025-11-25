package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;


public record AuthenticationRequestDTO(
        @NotBlank
        String email,

        @NotBlank
        String password
) {}
