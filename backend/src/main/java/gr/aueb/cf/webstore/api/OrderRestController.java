package gr.aueb.cf.webstore.api;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.exceptions.ValidationException;
import gr.aueb.cf.webstore.core.filters.OrderFilters;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.OrderInsertDTO;
import gr.aueb.cf.webstore.dto.OrderReadOnlyDTO;
import gr.aueb.cf.webstore.dto.OrderUpdateDTO;
import gr.aueb.cf.webstore.dto.ResponseMessageDTO;
import gr.aueb.cf.webstore.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderRestController {

    private final IOrderService orderService;

    @Operation(
            summary = "Create a new order",
            description = "Creates an order for a given user UUID.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Order created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "User or product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class)
                            )
                    )
            }
    )
    @PostMapping("/orders")
    public ResponseEntity<OrderReadOnlyDTO> createOrder(@Valid @RequestBody OrderInsertDTO orderInsertDTO, BindingResult bindingResult)
            throws ValidationException, AppObjectNotFoundException, AppObjectInvalidArgumentException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        OrderReadOnlyDTO dto = orderService.createOrder(orderInsertDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.id())
                .toUri();

        return ResponseEntity.created(location).body(dto);
    }
    @Operation(
            summary = "Get all orders paginated",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Orders returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Paginated.class,
                                            example = """
                                                    {
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "userUuid": "2f5de3be-1234-9a27-3abc-132418919151",
                                                          "status": "PENDING",
                                                          "totalPrice": 2999.99,
                                                          "createdAt": "2025-12-04T14:00:00"
                                                        }
                                                      ],
                                                      "currentPage": 0,
                                                      "pageSize": 10,
                                                      "totalPages": 3,
                                                      "numberOfElements": 1,
                                                      "totalElements": 50
                                                    }"""
                                    )
                            )
                    )
            }
    )
    @GetMapping("/orders")
    public ResponseEntity<Paginated<OrderReadOnlyDTO>> getPaginatedOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Paginated<OrderReadOnlyDTO> orders = orderService.getPaginatedOrders(page, size);

        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Get orders paginated and filtered",
            description = "Filter by status, orderId, user UUID or date range.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Orders returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Paginated.class,
                                            example = """
                                                    {
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "userUuid": "2f5de8be-1234-4a67-9abc-131415119151",
                                                          "status": "SHIPPED",
                                                          "totalPrice": 1499.00,
                                                          "createdAt": "2025-12-03T10:30:00"
                                                        }
                                                      ],
                                                      "currentPage": 1,
                                                      "pageSize": 10,
                                                      "totalPages": 2,
                                                      "numberOfElements": 2,
                                                      "totalElements": 2
                                                    }"""
                                    )
                            )
                    )
            }
    )
    @PostMapping("/orders/search")
    public ResponseEntity<Paginated<OrderReadOnlyDTO>> getFilteredAndPaginatedOrders(@Nullable @RequestBody OrderFilters filters) {

        if (filters == null) filters = OrderFilters.builder().build();

        Paginated<OrderReadOnlyDTO> paginated = orderService.getOrdersFilteredPaginated(filters);

        return ResponseEntity.ok(paginated);
    }

    @Operation(
            summary = "Get a single order by id",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Order returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Order not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderReadOnlyDTO> getOrderById(@PathVariable Long id) throws AppObjectNotFoundException, AppObjectNotAuthorizedException {

        return ResponseEntity.ok(orderService.getOneOrder(id));

    }

    @Operation(
            summary = "Update order status",
            description = "Updates the status of an order.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Order updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error or id mismatch",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Order not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class)
                            )
                    )
            }
    )
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderReadOnlyDTO> updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderUpdateDTO orderUpdateDTO, BindingResult bindingResult)
            throws ValidationException, AppObjectNotFoundException, AppObjectInvalidArgumentException {

        if (bindingResult.hasErrors()) throw new ValidationException(bindingResult);

        if (orderUpdateDTO.id() == null || !id.equals(orderUpdateDTO.id())) {

            throw new AppObjectInvalidArgumentException("Order", "Path id does not match body id");
        }

        OrderReadOnlyDTO updated = orderService.updateOrderStatus(orderUpdateDTO);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Get a single order by code",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Order returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Order not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class)
                            )
                    )
            }
    )
    @GetMapping("/orders/code/{orderCode}")
    public ResponseEntity<OrderReadOnlyDTO> getOrderByCode(@PathVariable String orderCode) throws AppObjectNotFoundException, AppObjectNotAuthorizedException {

        return ResponseEntity.ok(orderService.getOneOrderByCode(orderCode));
    }
}
