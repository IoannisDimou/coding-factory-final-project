package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.model.Address;
import java.math.BigDecimal;
import java.util.List;

public record OrderReadOnlyDTO(
        Long id,
        UserReadOnlyDTO user,
        Address shippingAddress,
        List<OrderItemReadOnlyDTO> items,
        List<PaymentReadOnlyDTO> payments,
        BigDecimal totalPrice,
        String status
) {}
