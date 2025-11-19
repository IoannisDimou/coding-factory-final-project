package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.OrderStatus;

public record OrderUpdateDTO(
        Long id,
        OrderStatus status
) {}

