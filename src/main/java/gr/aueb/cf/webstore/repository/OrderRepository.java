package gr.aueb.cf.webstore.repository;

import gr.aueb.cf.webstore.core.enums.OrderStatus;
import gr.aueb.cf.webstore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByTotalPrice(BigDecimal min, BigDecimal max);
}
