package gr.aueb.cf.webstore.repository;


import gr.aueb.cf.webstore.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {

    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductId(Long productId);
}
