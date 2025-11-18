package gr.aueb.cf.webstore.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "order_items")
public class OrderItem extends AbstractEntity{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private BigDecimal discount;

    @Column
    private BigDecimal tax;

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            order.getOrderItems().add(this);
        }
    }

    public BigDecimal getSubtotal() {

        BigDecimal quant = BigDecimal.valueOf(quantity);
        BigDecimal total = price.multiply(quant);

        if (discount != null) total = total.subtract(discount);

        if (tax != null) total = total.add(tax);

        return total;
    }


}
