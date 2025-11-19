package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.Role;

public record UserUpdateDTO(
        String firstname,
        String lastname,
        Role role,
        Boolean isActive,
        String phoneNumber
) {}

