package gr.aueb.cf.webstore.authentication;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.dto.AuthenticationRequestDTO;
import gr.aueb.cf.webstore.dto.AuthenticationResponseDTO;
import gr.aueb.cf.webstore.dto.TwoFactorChallengeDTO;
import gr.aueb.cf.webstore.dto.TwoFactorVerificationRequestDTO;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.repository.UserRepository;
import gr.aueb.cf.webstore.service.ITwoFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ITwoFactorService twoFactorService;
    private final UserRepository userRepository;

    public TwoFactorChallengeDTO authenticate(AuthenticationRequestDTO dto) throws AppObjectNotFoundException,
            AppObjectInvalidArgumentException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );

        User user = (User) authentication.getPrincipal();

        return twoFactorService.createTwoFactorChallenge(user.getEmail(), "EMAIL");
    }

    public AuthenticationResponseDTO completeAuthentication(TwoFactorVerificationRequestDTO request)
            throws AppObjectInvalidArgumentException, AppObjectNotFoundException {

        String email = twoFactorService.verifyTwoFactorCode(request);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppObjectNotFoundException("User", "User with email " + email + " not found"));

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthenticationResponseDTO(user.getFirstname(), user.getLastname(), user.getRole(), token, user.getUuid());
    }
}
