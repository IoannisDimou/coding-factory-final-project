package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.Role;

public record UserReadOnlyDTO(
        Long id,
        String uuid,
        String firstname,
        String lastname,
        String email,
        String phoneNumber,
        Role role,
        Boolean isActive
) {}
