package gr.aueb.cf.webstore.api;

import gr.aueb.cf.webstore.authentication.AuthenticationService;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.dto.*;
import gr.aueb.cf.webstore.service.IEmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationService authenticationService;
    private final IEmailVerificationService emailVerificationService;


    @Operation(
            summary = "Start authentication",
            description = "Checks email/password and sends a 2FA code to the user's email. " +
                    "Returns a two-factor token that must be used in the next step.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Credentials valid, 2FA challenge created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TwoFactorChallengeDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized – invalid credentials",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request – invalid parameters",
                            content = @Content
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TwoFactorChallengeDTO> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) throws AppObjectNotFoundException,
            AppObjectInvalidArgumentException {

        TwoFactorChallengeDTO challenge = authenticationService.authenticate(authenticationRequestDTO);

        return new ResponseEntity<>(challenge, HttpStatus.OK);
    }

    @Operation(
            summary = "Verify email",
            description = "Verifies a user's email using the token sent to their email.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email verified successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or expired token",
                            content = @Content
                    )
            }
    )
    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestBody @Valid EmailVerificationRequestDTO request) throws AppObjectInvalidArgumentException, AppObjectNotFoundException {

        emailVerificationService.verify(request.token());
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Complete authentication",
            description = "Verifies the two-factor token and code. Returns a JWT access token on success.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "authentication completed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthenticationResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request – invalid 2FA token/code",
                            content = @Content
                    )
            }
    )
    @PostMapping("/2fa/verify")
    public ResponseEntity<AuthenticationResponseDTO> completeAuth (@RequestBody TwoFactorVerificationRequestDTO requestDTO) throws AppObjectInvalidArgumentException,
            AppObjectNotFoundException {

        AuthenticationResponseDTO responseDTO = authenticationService.completeAuthentication(requestDTO);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

}



