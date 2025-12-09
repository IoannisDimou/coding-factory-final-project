package gr.aueb.cf.webstore.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO(
        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "Zipcode is required")
        String zipcode,

        @NotBlank(message = "Country is required")
        String country
) {}

