package gr.aueb.cf.webstore.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductFilters extends GenericFilters {

    @Nullable
    private String name;

    @Nullable
    private String category;

    @Nullable
    private BigDecimal minPrice;

    @Nullable
    private BigDecimal maxPrice;

}
