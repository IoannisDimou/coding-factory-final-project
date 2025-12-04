package gr.aueb.cf.webstore.api;

import gr.aueb.cf.webstore.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.core.exceptions.ValidationException;
import gr.aueb.cf.webstore.core.filters.Paginated;
import gr.aueb.cf.webstore.core.filters.UserFilters;
import gr.aueb.cf.webstore.dto.ResponseMessageDTO;
import gr.aueb.cf.webstore.dto.UserInsertDTO;
import gr.aueb.cf.webstore.dto.UserReadOnlyDTO;
import gr.aueb.cf.webstore.dto.UserUpdateDTO;
import gr.aueb.cf.webstore.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserService userService;

    @Operation(
            summary = "Create a new user",
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "User created",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PostMapping(value = "/users")
    public ResponseEntity<UserReadOnlyDTO> saveUser(@Valid @RequestBody UserInsertDTO userInsertDTO, BindingResult bindingResult) throws
            AppObjectAlreadyExists, ValidationException, AppObjectInvalidArgumentException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        UserReadOnlyDTO userReadOnlyDTO = userService.saveUser(userInsertDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(userReadOnlyDTO.uuid())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(userReadOnlyDTO);
    }

    @Operation(
            summary = "Get all users paginated",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Users returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Paginated.class,
                                    example = """
                                            {
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "uuid": "2f5de8be-1234-4a67-9abc-131415119151",
                                                  "firstname": "John",
                                                  "lastname": "Dew",
                                                  "email": "john.dew@example.com",
                                                  "phoneNumber": "9999999999",
                                                  "role": "USER",
                                                  "isActive": true
                                                }
                                              ],
                                              "currentPage": 0,
                                              "pageSize": 10,
                                              "totalPages": 5,
                                              "numberOfElements": 1,
                                              "totalElements": 100
                                            }"""
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)))
            }
    )
    @GetMapping("/users")
    public ResponseEntity<Paginated<UserReadOnlyDTO>> getPaginatedUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        Paginated<UserReadOnlyDTO> usersPage = userService.getPaginatedUsers(page, size);

        return ResponseEntity.ok(usersPage);
    }

    @Operation(
            summary = "Get all users paginated and filtered",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Users returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Paginated.class,
                                    example = """
                                            {
                                              "data": [
                                                {
                                                  "id": 1,
                                                  "uuid": "2f5de8be-1234-4a67-9abc-131415119151",
                                                  "firstname": "John",
                                                  "lastname": "Dew",
                                                  "email": "john.dew@example.com",
                                                  "phoneNumber": "9999999999",
                                                  "role": "USER",
                                                  "isActive": true
                                                }
                                              ],
                                              "currentPage": 0,
                                              "pageSize": 10,
                                              "totalPages": 5,
                                              "numberOfElements": 1,
                                              "totalElements": 100
                                            }"""
                            ))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class)))
            }
    )
    @PostMapping("/users/search")
    public ResponseEntity<Paginated<UserReadOnlyDTO>> getFilteredAndPaginatedUsers(@Nullable @RequestBody UserFilters filters) {

        if (filters == null) filters = UserFilters.builder().build();

        Paginated<UserReadOnlyDTO> paginated = userService.getUsersFilteredPaginated(filters);

        return ResponseEntity.ok(paginated);
    }

    @Operation(
            summary = "Get one user by uuid",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "User returned",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @GetMapping("/users/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> getUserByUuid(@PathVariable String uuid) throws AppObjectNotFoundException {

        return ResponseEntity.ok(userService.getUser(uuid));
    }

    @Operation(
            summary = "Update a user",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "User updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserReadOnlyDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "401", description = "Not Authenticated",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Access Denied",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500", description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessageDTO.class))
                    )
            }
    )
    @PutMapping(value = "/users/{uuid}")
    @PreAuthorize("#uuid == #userUpdateDTO.uuid()")
    public ResponseEntity<UserReadOnlyDTO> updateUser(@PathVariable String uuid, @Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                                      BindingResult bindingResult) throws ValidationException, AppObjectNotFoundException, AppObjectAlreadyExists, AppObjectInvalidArgumentException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        if (!uuid.equals(userUpdateDTO.uuid())) {

            throw new AppObjectInvalidArgumentException("User", "Path UUID does not match body UUID");
        }

        UserReadOnlyDTO userReadOnlyDTO = userService.updateUser(userUpdateDTO);

        return ResponseEntity.ok(userReadOnlyDTO);
    }
}
