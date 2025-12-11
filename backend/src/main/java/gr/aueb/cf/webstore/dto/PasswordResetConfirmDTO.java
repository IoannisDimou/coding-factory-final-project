package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetConfirmDTO (
        @NotBlank String token,

        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$", message = "Invalid Password")
        @NotBlank
        String newPassword
) {}
