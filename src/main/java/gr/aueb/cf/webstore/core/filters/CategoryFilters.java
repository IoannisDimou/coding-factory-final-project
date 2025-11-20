package gr.aueb.cf.webstore.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

public class CategoryFilters extends GenericFilters {

    @Nullable
    private String categoryId;

    @Nullable
    private String name;
}
