package gr.aueb.cf.webstore.mapper;

import gr.aueb.cf.webstore.dto.*;
import gr.aueb.cf.webstore.model.Category;
import gr.aueb.cf.webstore.model.Payment;
import gr.aueb.cf.webstore.model.Product;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(
                user.getId(),
                user.getUuid(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getIsActive()
        );
    }

    public User mapToUserEntity(UserInsertDTO dto) {

        User user = new User();

        user.setIsActive(dto.isActive());
        user.setFirstname(dto.firstname());
        user.setLastname(dto.lastname());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());

        return user;
    }

    public User mapToUserEntity(UserUpdateDTO dto) {

        User user = new User();

        user.setId(dto.id());
        user.setFirstname(dto.firstname());
        user.setLastname(dto.lastname());
        user.setPhoneNumber(dto.phoneNumber());
        user.setEmail(dto.email());
        user.setRole(dto.role());
        user.setIsActive(dto.isActive());
        user.setPassword(passwordEncoder.encode(dto.password()));

        return user;
    }

    public ProductReadOnlyDTO mapToProductReadOnlyDTO(Product product) {

        CategoryReadOnlyDTO categoryReadOnlyDTO = new CategoryReadOnlyDTO(
                product.getCategory().getId(),
                product.getCategory().getName()
        );

        List<ProductSpecReadOnlyDTO> specsDTO = product.getProductSpecs()
                .stream()
                .map(spec -> new ProductSpecReadOnlyDTO(
                        spec.getId(),
                        spec.getName(),
                        spec.getValue(),
                        product.getId()
                )).toList();

        return new ProductReadOnlyDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getSku(),
                product.getIsActive(),
                product.getBrand(),
                product.getImage(),
                categoryReadOnlyDTO,
                specsDTO
        );
    }

    public Product mapToProductEntity(ProductInsertDTO dto, Category category) {

        Product product = new Product();


        product.setCategory(category);
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setSku(dto.sku());
        product.setIsActive(dto.isActive());
        product.setBrand(dto.brand());
        product.setImage(dto.image());

        return product;

    }

    public Product mapToProductEntity(ProductUpdateDTO dto, Category category) {

        Product product = new Product();

        product.setId(dto.id());
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setSku(dto.sku());
        product.setIsActive(dto.isActive());
        product.setBrand(dto.brand());
        product.setImage(dto.image());
        product.setCategory(category);

        return product;
    }

    public Category mapToCategory(CategoryUpdateDTO dto) {

        Category category = new Category();

        category.setId(dto.id());
        category.setName(dto.name());
        return category;
    }

    public CategoryReadOnlyDTO mapToCategoryReadOnlyDTO(Category category) {
        return new CategoryReadOnlyDTO(
                category.getId(),
                category.getName()
        );
    }



    public PaymentReadOnlyDTO mapToPaymentReadOnlyDTO(Payment payment) {

        return new PaymentReadOnlyDTO(
                payment.getId(),
                payment.getTransactionId(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getCardBrand(),
                payment.getCardLastFourDigits(),
                payment.getOrder() != null ? payment.getOrder().getId() : null
        );
    }


    public OrderReadOnlyDTO mapToOrderReadOnlyDTO(Order order) {

        UserReadOnlyDTO userReadOnlyDTO = new UserReadOnlyDTO(
                order.getUser().getId(),
                order.getUser().getUuid(),
                order.getUser().getFirstname(),
                order.getUser().getLastname(),
                order.getUser().getEmail(),
                order.getUser().getPhoneNumber(),
                order.getUser().getRole(),
                order.getUser().getIsActive()
        );

        List<OrderItemReadOnlyDTO> itemsDTO = order.getOrderItems()
                .stream()
                .map(item -> new OrderItemReadOnlyDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getDiscount(),
                        item.getTax()
                )).toList();

        List<PaymentReadOnlyDTO> paymentsDTO = order.getPayments()
                .stream()
                .map(payment -> new PaymentReadOnlyDTO(
                        payment.getId(),
                        payment.getTransactionId(),
                        payment.getAmount(),
                        payment.getMethod(),
                        payment.getStatus(),
                        payment.getCardBrand(),
                        payment.getCardLastFourDigits(),
                        payment.getOrder().getId()
                ))
                .toList();


        return new OrderReadOnlyDTO(
                order.getId(),
                userReadOnlyDTO,
                order.getShippingAddress(),
                itemsDTO,
                paymentsDTO,
                order.getTotalPrice(),
                order.getStatus().name()
        );
    }

    public Order mapToOrderEntity(OrderUpdateDTO dto, Order existingOrder) {
        existingOrder.setStatus(dto.status());
        return existingOrder;
    }

}










