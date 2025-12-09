package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.enums.OrderStatus;
import gr.aueb.cf.webstore.core.enums.PaymentMethod;
import gr.aueb.cf.webstore.core.enums.PaymentStatus;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.PaymentConfirmationDTO;
import gr.aueb.cf.webstore.dto.PaymentReadOnlyDTO;
import gr.aueb.cf.webstore.dto.PaymentRequestDTO;
import gr.aueb.cf.webstore.mapper.Mapper;
import gr.aueb.cf.webstore.model.Order;
import gr.aueb.cf.webstore.model.Payment;
import gr.aueb.cf.webstore.repository.OrderRepository;
import gr.aueb.cf.webstore.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final Mapper mapper;
    private final IEmailService emailService;


    private static final Set<String> ALLOWED_TEST_CARDS = Set.of(
            "6666000000000000",
            "6666000000000001",
            "6666000000000002"
    );

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, Mapper mapper, IEmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.emailService = emailService;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PaymentReadOnlyDTO createPayment(PaymentRequestDTO paymentRequestDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException {

        Long orderId = paymentRequestDTO.orderId();
        PaymentMethod method = paymentRequestDTO.method();


        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppObjectNotFoundException(
                "Order", "Order with id " + orderId + " not found"));

        userCanAccessOrder(order);

        if (order.getStatus() == OrderStatus.CANCELLED)
            throw new AppObjectInvalidArgumentException(
                    "OrderStatus", "Cannot create payment for a cancelled order");

        boolean alreadyPaid = paymentRepository.findByOrderId(orderId).stream().anyMatch(
                p -> p.getStatus() == PaymentStatus.COMPLETED);

        if (alreadyPaid) throw new AppObjectInvalidArgumentException(
                "Payment", "Order " + orderId + " is already fully paid");


        BigDecimal orderTotal = order.getTotalPrice();

        if (orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppObjectInvalidArgumentException("Amount", "Order total must be positive to create a payment");
        }

        if (method == null) {
            throw new AppObjectInvalidArgumentException("PaymentMethod", "Payment method is required");
        }

        String cardBrand = "N/A";
        String cardLastFour = null;

        if (method == PaymentMethod.CREDIT_CARD) {
            String normalized = normalizeCardNumber(paymentRequestDTO.cardNumber());

            if (normalized.isEmpty()) {
                throw new AppObjectInvalidArgumentException("CardNumber", "Card number is required for credit card payments");
            }

            if (!isAllowedTestCard(normalized)) {
                throw new AppObjectInvalidArgumentException("CardNumber", "Real cards are not accepted. Use a test card like 9999 0000 0000 0000.");
            }

            cardBrand = "TEST_CARD";
            cardLastFour = normalized.substring(normalized.length() - 4);
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmount(orderTotal);
        payment.setCardBrand(cardBrand);
        payment.setCardLastFourDigits(cardLastFour);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentToken(UUID.randomUUID().toString());

        Payment savedPayment = paymentRepository.save(payment);

        log.info("Payment created successfully. id={}, orderId={}, status={}, method={}", savedPayment.getId(),
                orderId, savedPayment.getStatus(), savedPayment.getMethod());

        return mapper.mapToPaymentReadOnlyDTO(savedPayment);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PaymentReadOnlyDTO confirmPayment(PaymentConfirmationDTO paymentConfirmationDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException {

        String token = paymentConfirmationDTO.paymentToken();

        Payment payment = paymentRepository.findByPaymentToken(token).orElseThrow(() -> new AppObjectNotFoundException(
                        "Payment", "Payment with token " + token + " not found"));

        Order order = payment.getOrder();
        userCanAccessOrder(order);

        if (payment.getStatus() == PaymentStatus.COMPLETED) { throw new AppObjectInvalidArgumentException(
                    "Payment", "Payment is already completed");
        }

        if (payment.getStatus() == PaymentStatus.FAILED) { throw new AppObjectInvalidArgumentException(
                "Payment", "Cannot confirm a failed payment");
        }

        payment.setStatus(PaymentStatus.COMPLETED);

        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Payment with id={} confirmed successfully (token={}).", updatedPayment.getId(), token);

        if (order != null) {
            emailService.sendOrderConfirmation(order);
        }

        return mapper.mapToPaymentReadOnlyDTO(updatedPayment);
    }

    @Override
    public PaymentReadOnlyDTO getPayment(Long id) throws AppObjectNotFoundException {

        return paymentRepository.findById(id)
                .map(mapper::mapToPaymentReadOnlyDTO)
                .orElseThrow(() -> new AppObjectNotFoundException(
                        "Payment", "Payment with id " + id + " not found"));
    }

    @Override
    public List<PaymentReadOnlyDTO> getPaymentsForOrder(Long orderId) throws AppObjectNotFoundException {

        orderRepository.findById(orderId).orElseThrow(() -> new AppObjectNotFoundException(
                        "Order", "Order with id " + orderId + " not found"));

        return paymentRepository.findByOrderId(orderId).stream()
                .map(mapper::mapToPaymentReadOnlyDTO).toList();
    }

    private String normalizeCardNumber(String cardNumber) {
        return cardNumber == null ? "" : cardNumber.replaceAll("\\D", "");
    }

    private boolean isAllowedTestCard(String normalized) {
        return ALLOWED_TEST_CARDS.contains(normalized);
    }

    @Override
    public Paginated<PaymentReadOnlyDTO> getPaginatedPayments(int page, int size) {

        String defaultSort = "id";

        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).descending());

        Page<Payment> payments = paymentRepository.findAll(pageable);

        return Paginated.fromPage(payments.map(mapper::mapToPaymentReadOnlyDTO));
    }

    private void userCanAccessOrder(Order order) throws AppObjectInvalidArgumentException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) throw new AppObjectInvalidArgumentException("Order", "You are not allowed to access this order");

        String username = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()

                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        boolean isOwner = order.getUser() != null && username.equals(order.getUser().getEmail());

        if (!isOwner && !isAdmin) throw new AppObjectInvalidArgumentException("Order", "You are not allowed to access this order");

    }

}
