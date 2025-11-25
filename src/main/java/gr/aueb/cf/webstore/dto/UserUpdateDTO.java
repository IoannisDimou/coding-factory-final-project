package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.Role;
import jakarta.validation.constraints.Pattern;

public record UserUpdateDTO(
        Long id,
        String firstname,
        String lastname,
        Role role,
        Boolean isActive,
        String phoneNumber,

        @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$", message = "Invalid Password")
        String password,

        String email

) {}

