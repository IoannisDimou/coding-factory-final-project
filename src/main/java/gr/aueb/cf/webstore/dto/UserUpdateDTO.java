package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.Role;

public record UserUpdateDTO(
        Long id,
        String firstname,
        String lastname,
        Role role,
        Boolean isActive,
        String phoneNumber,
        String password,
        String email
) {}

