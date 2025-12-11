package gr.aueb.cf.webstore.service;

import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PasswordResetService implements IPasswordResetService {

    private static final String KEY_PREFIX = "password-reset:";
    private static final long EXPIRATION_MINUTES = 10L;

    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Autowired
    public PasswordResetService(StringRedisTemplate redisTemplate, UserRepository userRepository, IEmailService emailService, PasswordEncoder passwordEncoder) {
        this.redisTemplate = redisTemplate;
        this. userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createToken(String email) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            log.info("Password reset requested for non-existing email={}", email);
            return;
        }

        String token = UUID.randomUUID().toString();
        String key = KEY_PREFIX + token;

        redisTemplate
                .opsForValue()
                .set(key, email, Duration.ofMinutes(EXPIRATION_MINUTES));

        String resetLink = frontendBaseUrl + "/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(email, resetLink);

        log.info("Sent password reset token for {}", email);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) throws AppObjectInvalidArgumentException, AppObjectNotFoundException {

        String key = KEY_PREFIX + token;
        String email = redisTemplate.opsForValue().get(key);

        if (email == null) {
            throw new AppObjectInvalidArgumentException("PasswordReset", "Invalid or expired password reset token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppObjectNotFoundException("User", "User with email " + email + " not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redisTemplate.delete(key);

        log.info("Password reset for user {}", email);
    }
}
