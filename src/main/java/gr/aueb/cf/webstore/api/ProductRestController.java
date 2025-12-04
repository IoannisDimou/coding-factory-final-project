package gr.aueb.cf.webstore.api;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.exceptions.ValidationException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.core.filters.ProductFilters;
import gr.aueb.cf.webstore.dto.*;
import gr.aueb.cf.webstore.service.IProductService;
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
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductRestController {

    private final IProductService productService;

    @Operation(
            summary = "Create a new product",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Product created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Category not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Product already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PostMapping(value = "/products")
    public ResponseEntity<ProductReadOnlyDTO> saveProduct(@Valid @RequestBody ProductInsertDTO productInsertDTO, BindingResult bindingResult) throws
            AppObjectAlreadyExists, AppObjectNotFoundException, AppObjectInvalidArgumentException, ValidationException {

            if (bindingResult.hasErrors()) {
                throw new ValidationException(bindingResult);
            }

            ProductReadOnlyDTO dto = productService.saveProduct(productInsertDTO);

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
            summary = "Get all products paginated",
            description = "returns paginated list of products",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Products returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Paginated.class,
                                            example = """
                                                    {
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "name": "RTX 4060",
                                                          "description": "NVIDIA GeForce RTX 4060 8GB",
                                                          "price": 389.99,
                                                          "stock": 12,
                                                          "sku": "GPU-RTX4060-001",
                                                          "isActive": true,
                                                          "brand": "NVIDIA",
                                                          "image": "https://example.com/images/rtx4060.jpg",
                                                          "category": {
                                                            "id": 1,
                                                            "name": "GPU",
                                                            "isActive": true
                                                          },
                                                          "specs": [
                                                            { "id": 2, "name": "Chipset", "value": "RTX 4060", "productId": 1 },
                                                            { "id": 3, "name": "VRAM", "value": "8GB GDDR6", "productId": 1 }
                                                          ]
                                                        }
                                                      ],
                                                      "currentPage": 0,
                                                      "pageSize": 10,
                                                      "totalPages": 3,
                                                      "numberOfElements": 1,
                                                      "totalElements": 60
                                                    }"""
                                    ))
                    )
            }
    )
    @GetMapping("/products")
    public ResponseEntity<Paginated<ProductReadOnlyDTO>> getPaginatedProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Paginated<ProductReadOnlyDTO> productsPage = productService.getPaginatedProducts(page, size);

        return ResponseEntity.ok(productsPage);
    }

    @Operation(
            summary = "Get all products paginated and filtered",
            description = "Public endpoint – supports filtering by name, category, price range, brand and specs.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Products returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Paginated.class,
                                            example = """
                                                    {
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "name": "RTX 4060",
                                                          "description": "NVIDIA GeForce RTX 4060 8GB",
                                                          "price": 389.99,
                                                          "stock": 12,
                                                          "sku": "GPU-RTX4060-001",
                                                          "isActive": true,
                                                          "brand": "NVIDIA",
                                                          "image": "https://example.com/images/rtx4060.jpg",
                                                          "category": {
                                                            "id": 1,
                                                            "name": "GPU",
                                                            "isActive": true
                                                          },
                                                          "specs": [
                                                            { "id": 10, "name": "Chipset", "value": "RTX 4060", "productId": 1 },
                                                            { "id": 11, "name": "VRAM", "value": "8GB GDDR6", "productId": 1 }
                                                          ]
                                                        }
                                                      ],
                                                      "currentPage": 0,
                                                      "pageSize": 10,
                                                      "totalPages": 3,
                                                      "numberOfElements": 1,
                                                      "totalElements": 60
                                                    }"""
                                    ))
                    )
            }
    )
    @PostMapping("/products/search")
    public ResponseEntity<Paginated<ProductReadOnlyDTO>> getFilteredAndPaginatedProducts(@Nullable @RequestBody ProductFilters filters) {

        if (filters == null) filters = ProductFilters.builder().build();

        Paginated<ProductReadOnlyDTO> paginated = productService.getProductsFilteredPaginated(filters);

        return ResponseEntity.ok(paginated);
    }

    @Operation(
            summary = "Get one product by id",
            description = "returns a single product by database id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Product returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductReadOnlyDTO> getOneProduct(@PathVariable Long id) throws AppObjectNotFoundException {

        ProductReadOnlyDTO dto = productService.getOneProduct(id);

        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Update a product",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Product updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Product already exists",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error or path/body mismatch",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductReadOnlyDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO productUpdateDTO, BindingResult bindingResult) throws
            ValidationException, AppObjectNotFoundException, AppObjectAlreadyExists, AppObjectInvalidArgumentException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        if (productUpdateDTO.id() == null || !id.equals(productUpdateDTO.id())) {
            throw new AppObjectInvalidArgumentException("Product", "Path id does not match body id");
        }

        ProductReadOnlyDTO dto = productService.updateProduct(productUpdateDTO);

        return ResponseEntity.ok(dto);

    }

    @Operation(
            summary = "Get specs for a product",
            description = "Returns all specs for a given product.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Specs returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductSpecReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping("/products/{id}/specs")
    public ResponseEntity<List<ProductSpecReadOnlyDTO>> getProductSpecs(@PathVariable Long id) throws AppObjectNotFoundException {

        List<ProductSpecReadOnlyDTO> specs = productService.getProductSpecs(id);

        return ResponseEntity.ok(specs);
    }

    @Operation(
            summary = "Add a spec to a product",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Spec added",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProductSpecReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Product not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Spec with same name already exists for this product",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PostMapping("/products/{id}/specs")
    public ResponseEntity<ProductSpecReadOnlyDTO> addProductSpec(@PathVariable Long id, @Valid @RequestBody ProductSpecInsertDTO dto, BindingResult bindingResult)
            throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExists {

        if (bindingResult.hasErrors()) {

            throw new ValidationException(bindingResult);
        }

        ProductSpecReadOnlyDTO specDTO = productService.addProductSpec(id, dto);

        return ResponseEntity.status(201).body(specDTO);
    }
}


