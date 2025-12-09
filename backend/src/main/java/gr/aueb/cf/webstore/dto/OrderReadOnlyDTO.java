package gr.aueb.cf.webstore.dto;

import gr.aueb.cf.webstore.core.enums.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record OrderReadOnlyDTO(
        Long id,
        String orderCode,
        UserReadOnlyDTO user,
        AddressDTO shippingAddress,
        List<OrderItemReadOnlyDTO> items,
        List<PaymentReadOnlyDTO> payments,
        BigDecimal totalPrice,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
