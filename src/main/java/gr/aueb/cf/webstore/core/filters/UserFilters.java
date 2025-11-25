package gr.aueb.cf.webstore.core.filters;


import gr.aueb.cf.webstore.core.enums.Role;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserFilters extends GenericFilters {

    @Nullable
    private String uuid;

    @Nullable
    private String firstname;

    @Nullable
    private String lastname;

    @Nullable
    private Boolean isActive;

    @Nullable
    private Role role;

    @Nullable
    private String email;

}
