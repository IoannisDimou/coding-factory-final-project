package gr.aueb.cf.webstore.model;

import gr.aueb.cf.webstore.core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "orders")
public class Order extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(mappedBy = "order", orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;


    @Column(nullable = false)
    private Double totalPrice;

    @Embedded
    private Address shippingAddress;


    @PrePersist
    public void initialStatus() {
        if (status == null) status = OrderStatus.PENDING;
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setOrder(this);
    }

    public void removePayment(Payment payment) {
        payments.remove(payment);
        payment.setOrder(null);
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }
}
