package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService implements IEmailVerificationService {

    private static final String KEY_PREFIX = "email-verification:";
    private static final long EXPIRATION_MINUTES = 30L;

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final IEmailService emailService;

//    @Value("${app.frontend.base-url:http://localhost:5173}")
//    private String frontendBaseUrl;

    @Override
    public void createAndSendToken(User user) {

        String token = UUID.randomUUID().toString();
        String key = KEY_PREFIX + token;

        redisTemplate
                .opsForValue()
                .set(key, user.getEmail(), Duration.ofMinutes(EXPIRATION_MINUTES));

        //String verificationLink = frontendBaseUrl + "/verify-email?token=" + token;
        String verificationLink = "http://localhost:8080/api/auth/verify-email?token=" + token;


        emailService.sendEmailVerification(user.getEmail(), verificationLink, token);

        log.info("Sent email verification token for user {} with token {}", user.getEmail(), token);
    }

    @Override
    @Transactional
    public void verify(String token) throws AppObjectInvalidArgumentException, AppObjectNotFoundException {

        String key = KEY_PREFIX + token;
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw new AppObjectInvalidArgumentException("EmailVerification", "Invalid or expired email verification token");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);

        User user = userOpt.orElseThrow(() -> new AppObjectNotFoundException("User", "User with email " + email + " not found"));

        user.setEmailVerified(true);
        user.setIsActive(true);

        userRepository.save(user);
        redisTemplate.delete(key);

        log.info("Email verified for user {}", email);
    }
}
