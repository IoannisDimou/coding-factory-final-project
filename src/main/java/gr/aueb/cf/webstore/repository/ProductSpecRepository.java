package gr.aueb.cf.webstore.repository;

import gr.aueb.cf.webstore.model.ProductSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long>, JpaSpecificationExecutor<ProductSpec> {

    List<ProductSpec> findByProductId(Long productId);

    List<ProductSpec> findByName(String name);
}
