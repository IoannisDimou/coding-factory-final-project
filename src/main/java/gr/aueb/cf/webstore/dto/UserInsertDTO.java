package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.Role;
import gr.aueb.cf.webstore.validator.EmailOrPhone;
import jakarta.validation.constraints.*;

@EmailOrPhone
public record UserInsertDTO(
        @NotBlank(message = "First value is required")
        String firstname,

        @NotBlank(message = "Last value is required")
        String lastname,

        @Email(message = "Invalid email")
        String email,

        @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
        String phoneNumber,

        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$",
                message = "Invalid Password")
        String password,

        @NotNull(message = "Role is required")
        Role role,

        boolean isActive
) {}
