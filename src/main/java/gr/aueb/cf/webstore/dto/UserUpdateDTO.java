package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDTO(

        @NotBlank(message = "UUID is required")
        String uuid,

        String firstname,
        String lastname,

        @NotNull(message = "Role is required")
        Role role,

        Boolean isActive,

        @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
        String phoneNumber,

        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$", message = "Invalid Password")
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        String email

) {}

