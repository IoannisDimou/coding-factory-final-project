package gr.aueb.cf.webstore.model;

import gr.aueb.cf.webstore.core.enums.PaymentMethod;
import gr.aueb.cf.webstore.core.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "payments")
public class Payment extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 4)
    private String cardLastFourDigits;

    @Column(nullable = false)
    private String cardBrand;

    @Column(unique = true, nullable = false)
    private String transactionId;

    @Column(unique = true, nullable = false)
    private String paymentToken;

}
