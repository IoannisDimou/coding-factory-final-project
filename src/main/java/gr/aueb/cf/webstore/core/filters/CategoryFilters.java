package gr.aueb.cf.webstore.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CategoryFilters extends GenericFilters {

    @Nullable
    private String categoryId;

    @Nullable
    private String name;
}
