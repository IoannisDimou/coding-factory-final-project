package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.Role;

public record AuthenticationResponseDTO(
        String firstname,
        String lastname,
        Role role,
        String token
) {}
