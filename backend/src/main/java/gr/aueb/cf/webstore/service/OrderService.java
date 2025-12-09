package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.enums.OrderStatus;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.filters.OrderFilters;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.*;
import gr.aueb.cf.webstore.mapper.Mapper;
import gr.aueb.cf.webstore.model.*;
import gr.aueb.cf.webstore.repository.OrderRepository;
import gr.aueb.cf.webstore.repository.ProductRepository;
import gr.aueb.cf.webstore.repository.UserRepository;
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
import java.math.RoundingMode;

@Service
@Slf4j
public class OrderService implements  IOrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final Mapper mapper;
    private static final BigDecimal TAX_RATE = new BigDecimal("0.24");


    @Autowired
    public OrderService(OrderRepository orderRepository, Mapper mapper, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderReadOnlyDTO createOrder(OrderInsertDTO orderInsertDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException {

        if (orderInsertDTO.items() == null || orderInsertDTO.items().isEmpty()) throw new AppObjectInvalidArgumentException(
                    "Order", "Order must contain at least one item");

        if (orderInsertDTO.shippingAddress() == null) throw new AppObjectInvalidArgumentException("Address", "Shipping address is required");

        User user = userRepository.findByUuid(orderInsertDTO.userUuid()).orElseThrow(() -> new AppObjectNotFoundException(
                        "User", "User with uuid " + orderInsertDTO.userUuid() + " not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        AddressDTO addrDto = orderInsertDTO.shippingAddress();

        Address address = new Address(
                addrDto.street(),
                addrDto.city(),
                addrDto.zipcode(),
                addrDto.country()
        );

        order.setShippingAddress(address);

        for (OrderItemInsertDTO itemDTO : orderInsertDTO.items()) {

            Product product = productRepository.findById(itemDTO.productId()).orElseThrow(() -> new AppObjectNotFoundException(
                            "Product", "Product with id " + itemDTO.productId() + " not found"));

            if (Boolean.FALSE.equals(product.getIsActive())) throw new AppObjectInvalidArgumentException("Product",
                    "Product with id " + product.getId() + " is inactive");

            if (product.getStock() < itemDTO.quantity()) throw new AppObjectInvalidArgumentException(
                        "Stock", "Insufficient stock for product " + product.getId());

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setPrice(product.getPrice());

            BigDecimal quantity = BigDecimal.valueOf(itemDTO.quantity());
            BigDecimal lineGross = product.getPrice().multiply(quantity);
            BigDecimal lineNet = lineGross.divide(BigDecimal.ONE.add(TAX_RATE), 2, RoundingMode.HALF_UP);
            BigDecimal lineTax = lineGross.subtract(lineNet);

            orderItem.setTax(lineTax);

            order.addOrderItem(orderItem);

            product.setStock(product.getStock() - itemDTO.quantity());
        }

        order.setTotalPrice(order.calculateTotal());

        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully. id={}, userId={}, userUuid={}", savedOrder.getId(), user.getId(), user.getUuid());

        return mapper.mapToOrderReadOnlyDTO(savedOrder);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderReadOnlyDTO updateOrderStatus(OrderUpdateDTO orderUpdateDTO) throws AppObjectNotFoundException, AppObjectInvalidArgumentException {

        if (orderUpdateDTO.status() == null) throw new AppObjectInvalidArgumentException("OrderStatus", "Order status is required");

        Order existingOrder = orderRepository.findById(orderUpdateDTO.id()).orElseThrow(() -> new AppObjectNotFoundException(
                        "Order",
                        "Order with id " + orderUpdateDTO.id() + " not found"));

        existingOrder.setStatus(orderUpdateDTO.status());

        Order updatedOrder = orderRepository.save(existingOrder);

        log.info("Order with id={} updated successfully to status={}.", updatedOrder.getId(), updatedOrder.getStatus());

        return mapper.mapToOrderReadOnlyDTO(updatedOrder);
    }

    @Override
    @Transactional
    public OrderReadOnlyDTO getOneOrder(Long id) throws AppObjectNotFoundException, AppObjectNotAuthorizedException {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppObjectNotFoundException("Order", "Order with id " + id + " not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AppObjectNotAuthorizedException("Order", "You are not allowed to access this order");
        }

        String username = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()

                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        boolean isOwner = order.getUser() != null && username.equals(order.getUser().getEmail());

        if (!isOwner && !isAdmin) throw new AppObjectNotAuthorizedException("Order", "You are not allowed to access this order");

        return mapper.mapToOrderReadOnlyDTO(order);

    }

    @Override
    @Transactional
    public Paginated<OrderReadOnlyDTO> getPaginatedOrders(int page, int size) {

        String defaultSort = "id";

        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        Page<Order> pagedOrders = orderRepository.findAll(pageable);

        log.debug("Paginated orders returned successfully with page={} and size={}", page, size);

        return Paginated.fromPage(pagedOrders.map(mapper::mapToOrderReadOnlyDTO));
    }

    @Override
    @Transactional
    public Paginated<OrderReadOnlyDTO> getOrdersFilteredPaginated(OrderFilters orderFilters) {

        var page = orderRepository.findAll(orderFilters.getPageable());

        log.debug("Filtered and paginated orders returned successfully with page={} and size={}", orderFilters.getPage(), orderFilters.getPageSize());

        return Paginated.fromPage(page.map(mapper::mapToOrderReadOnlyDTO));
    }
}
