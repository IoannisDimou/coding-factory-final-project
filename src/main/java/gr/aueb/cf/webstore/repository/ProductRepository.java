package gr.aueb.cf.webstore.repository;

import gr.aueb.cf.webstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    List<Product> findByCategoryId(Long categoryId);
    Optional<Product> findBySku(String sku);
}
