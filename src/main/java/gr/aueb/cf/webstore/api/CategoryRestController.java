package gr.aueb.cf.webstore.api;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.exceptions.ValidationException;
import gr.aueb.cf.webstore.core.filters.CategoryFilters;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.dto.CategoryInsertDTO;
import gr.aueb.cf.webstore.dto.CategoryReadOnlyDTO;
import gr.aueb.cf.webstore.dto.CategoryUpdateDTO;
import gr.aueb.cf.webstore.dto.ResponseMessageDTO;
import gr.aueb.cf.webstore.service.ICategoryService;
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
public class CategoryRestController {

    private final ICategoryService categoryService;

    @Operation(
            summary = "Create a new category",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "Category created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Category already exists",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PostMapping("/categories")
    public ResponseEntity<CategoryReadOnlyDTO> saveCategory(@Valid @RequestBody CategoryInsertDTO categoryInsertDTO, BindingResult bindingResult)
            throws ValidationException, AppObjectAlreadyExists, AppObjectInvalidArgumentException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        CategoryReadOnlyDTO dto = categoryService.saveCategory(categoryInsertDTO);

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
            summary = "Get all categories paginated",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Categories returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Paginated.class,
                                            example = """
                                                    {
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "name": "GPUs",
                                                          "description": "Graphics cards",
                                                          "isActive": true
                                                        }
                                                      ],
                                                      "currentPage": 0,
                                                      "pageSize": 10,
                                                      "totalPages": 3,
                                                      "numberOfElements": 1,
                                                      "totalElements": 50
                                                    }"""
                                    ))
                    )
            }
    )
    @GetMapping("/categories")
    public ResponseEntity<Paginated<CategoryReadOnlyDTO>> getPaginatedCategories(@RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size) {

        Paginated<CategoryReadOnlyDTO> categoriesPage =
                categoryService.getPaginatedCategories(page, size);

        return ResponseEntity.ok(categoriesPage);
    }
    @Operation(
            summary = "Get all categories paginated and filtered",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Categories returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Paginated.class,
                                            example = """
                                                    {
                                                      "data": [
                                                        {
                                                          "id": 1,
                                                          "name": "GPUs",
                                                          "description": "Graphics cards",
                                                          "isActive": true
                                                        }
                                                      ],
                                                      "currentPage": 0,
                                                      "pageSize": 10,
                                                      "totalPages": 1,
                                                      "numberOfElements": 1,
                                                      "totalElements": 1
                                                    }"""
                                    ))
                    )
            }
    )
    @PostMapping("/categories/search")
    public ResponseEntity<Paginated<CategoryReadOnlyDTO>> getFilteredAndPaginatedCategories(@Nullable @RequestBody CategoryFilters filters) {

        if (filters == null) {
            filters = CategoryFilters.builder().build();
        }

        Paginated<CategoryReadOnlyDTO> paginated = categoryService.getCategoriesFilteredPaginated(filters);

        return ResponseEntity.ok(paginated);
    }

    @Operation(
            summary = "Get one category by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Category returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Category not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryReadOnlyDTO> getCategoryById(@PathVariable Long id) throws AppObjectNotFoundException {

        return ResponseEntity.ok(categoryService.getOneCategory(id));
    }

    @Operation(
            summary = "Update a category",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Category updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "Category already exists",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "Category not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryReadOnlyDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO,
            BindingResult bindingResult) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExists,
            AppObjectInvalidArgumentException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        if (categoryUpdateDTO.id() == null || !id.equals(categoryUpdateDTO.id())) {

            throw new AppObjectInvalidArgumentException("Category", "Path id does not match body id");
        }

        CategoryReadOnlyDTO updated = categoryService.updateCategory(categoryUpdateDTO);

        return ResponseEntity.ok(updated);
    }
}
