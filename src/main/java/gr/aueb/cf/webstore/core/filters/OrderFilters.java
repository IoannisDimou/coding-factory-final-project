package gr.aueb.cf.webstore.core.filters;

import gr.aueb.cf.webstore.core.enums.OrderStatus;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OrderFilters extends GenericFilters {

    @Nullable
    private OrderStatus status;

    @Nullable
    private String orderId;

    @Nullable
    private String userUuid;

    @Nullable
    private LocalDate dateFrom;

    @Nullable
    private LocalDate dateTo;


}
