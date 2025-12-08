package gr.aueb.cf.webstore.core.specifications;

import gr.aueb.cf.webstore.model.Category;
import gr.aueb.cf.webstore.model.Product;
import gr.aueb.cf.webstore.model.ProductSpec;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> stringFieldLike(String field, String value) {

        return (root, query, criteriaBuilder) -> {

            if (value == null || value.trim().isEmpty()) {

                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.like(criteriaBuilder.upper(root.get(field)), "%" + value.trim().toUpperCase() + "%");
        };
    }

    public static Specification<Product> categoryNameLike(String categoryName) {

        return (root, query, criteriaBuilder) -> {

            if (categoryName == null || categoryName.trim().isEmpty()) {

                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            Join<Product, Category> category = root.join("category");

            return criteriaBuilder.like(
                    criteriaBuilder.upper(category.get("name")), "%" + categoryName.trim().toUpperCase() + "%");
        };
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null && maxPrice == null) {

                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            if (minPrice != null && maxPrice != null) {

                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
            if (minPrice != null) {

                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else {

                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
        };
    }

    public static Specification<Product> brandLike(String brand) {

        return (root, query, criteriaBuilder) -> {

            if (brand == null || brand.trim().isEmpty()) {

                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            return criteriaBuilder.like(
                    criteriaBuilder.upper(root.get("brand")), "%" + brand.trim().toUpperCase() + "%");
        };
    }

    public static Specification<Product> specLike(String specName, String specValue) {

        return (root, query, criteriaBuilder) -> {

            boolean noName = (specName == null || specName.trim().isEmpty());

            boolean noValue = (specValue == null || specValue.trim().isEmpty());


            if (noName && noValue) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            Join<Product, ProductSpec> specJoin = root.join("productSpecs");

            var predicate = criteriaBuilder.conjunction();

            if (!noName) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(
                                criteriaBuilder.upper(specJoin.get("name")), specName.trim().toUpperCase()));
            }

            if (!noValue) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like
                        (criteriaBuilder.upper(specJoin.get("value")), "%" + specValue.trim().toUpperCase() + "%"));
            }

            if (query != null) query.distinct(true);

            return predicate;
        };
    }

}
