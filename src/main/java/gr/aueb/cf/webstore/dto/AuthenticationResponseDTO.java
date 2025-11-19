package gr.aueb.cf.webstore.dto;

public record AuthenticationResponseDTO(
        String firstname,
        String lastname,
        String token
) {}
