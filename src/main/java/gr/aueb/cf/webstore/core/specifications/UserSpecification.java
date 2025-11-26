package gr.aueb.cf.webstore.core.specifications;

import gr.aueb.cf.webstore.core.enums.Role;
import gr.aueb.cf.webstore.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> stringFieldLike(String field, String value) {

        return (root, query, criteriaBuilder) -> {

            if (value == null || value.trim().isEmpty()) {

                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            return criteriaBuilder.like(
                    criteriaBuilder.upper(root.get(field)),
                    "%" + value.toUpperCase() + "%"
            );
        };
    }

    public static Specification<User> userIsActive(Boolean isActive) {

        return (root, query, criteriaBuilder) -> {

            if (isActive == null) {

                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            return criteriaBuilder.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<User> userRoleIs(Role role) {

        return (root, query, criteriaBuilder) -> {

            if (role == null) {

                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            return criteriaBuilder.equal(root.get("role"), role);

        };
    }
}
