package gr.aueb.cf.webstore.dto;

public record AddressDTO(
        String street,
        String city,
        String zipcode,
        String country
) {}

