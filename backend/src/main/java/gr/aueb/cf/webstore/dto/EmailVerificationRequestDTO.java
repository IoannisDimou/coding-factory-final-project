package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequestDTO(
        @NotBlank
        String token
) {}
