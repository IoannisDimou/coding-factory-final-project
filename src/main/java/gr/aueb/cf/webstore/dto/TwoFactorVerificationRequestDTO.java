package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;

public record TwoFactorVerificationRequestDTO(
        @NotBlank String twoFactorToken,
        @NotBlank String code
) {}
