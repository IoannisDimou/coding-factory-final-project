package gr.aueb.cf.webstore.model;

import jakarta.persistence.*;
import lombok.*;

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
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column
    private Double discount = 0.0;

    @Column
    private Double tax = 0.0;

    public void setOrder(Order order) {
        this.order = order;
        if (order != null) {
            order.getOrderItems().add(this);
        }
    }

    public Double getSubtotal() {
        return (price * quantity) - discount + tax;
    }


}
