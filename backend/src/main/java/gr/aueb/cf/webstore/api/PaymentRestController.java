package gr.aueb.cf.webstore.api;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.exceptions.ValidationException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.PaymentConfirmationDTO;
import gr.aueb.cf.webstore.dto.PaymentReadOnlyDTO;
import gr.aueb.cf.webstore.dto.PaymentRequestDTO;
import gr.aueb.cf.webstore.dto.ResponseMessageDTO;
import gr.aueb.cf.webstore.service.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentRestController {

    private final IPaymentService paymentService;

    @Operation(
            summary = "Create a payment for an order",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Payment created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Order not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<PaymentReadOnlyDTO> createPayment(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO, BindingResult bindingResult)
            throws ValidationException, AppObjectInvalidArgumentException, AppObjectNotFoundException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        PaymentReadOnlyDTO dto = paymentService.createPayment(paymentRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(dto);
    }

    @Operation(
            summary = "Confirm a payment",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Payment confirmed",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Invalid payment token",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Payment not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PostMapping("/confirm")
    public ResponseEntity<PaymentReadOnlyDTO> confirmPayment(@Valid @RequestBody PaymentConfirmationDTO paymentConfirmationDTO, BindingResult bindingResult)
            throws ValidationException, AppObjectInvalidArgumentException, AppObjectNotFoundException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        PaymentReadOnlyDTO dto = paymentService.confirmPayment(paymentConfirmationDTO);

        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Get one payment by id",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Payment returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Payment not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PaymentReadOnlyDTO> getPayment(@PathVariable Long id) throws AppObjectNotFoundException {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @Operation(
            summary = "Get all payments for a specific order",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Payments returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Order not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentReadOnlyDTO>> getPaymentsForOrder(@PathVariable Long orderId) throws AppObjectNotFoundException {

        List<PaymentReadOnlyDTO> payments = paymentService.getPaymentsForOrder(orderId);

        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all payments paginated",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Payments returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Paginated.class,
                                            example = """
                                                    {
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "transactionId": "3a5de8ce-1111-3h31-4bbb-661415159159",
                                                          "amount": 59.99,
                                                          "method": "CREDIT_CARD",
                                                          "status": "COMPLETED",
                                                          "cardBrand": "TEST_CARD",
                                                          "cardLastFourDigits": "9999",
                                                          "orderId": 8
                                                        }
                                                      ],
                                                      "currentPage": 0,
                                                      "pageSize": 10,
                                                      "totalPages": 10,
                                                      "numberOfElements": 1,
                                                      "totalElements": 100
                                                    }"""
                                    ))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Paginated<PaymentReadOnlyDTO>> getPaginatedPayments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Paginated<PaymentReadOnlyDTO> paginated = paymentService.getPaginatedPayments(page, size);

        return ResponseEntity.ok(paginated);
    }
}
