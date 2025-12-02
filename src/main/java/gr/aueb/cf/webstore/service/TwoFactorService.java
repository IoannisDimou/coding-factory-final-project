package gr.aueb.cf.webstore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.aueb.cf.webstore.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.webstore.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.webstore.dto.TwoFactorChallengeDTO;
import gr.aueb.cf.webstore.dto.TwoFactorRedisDTO;
import gr.aueb.cf.webstore.dto.TwoFactorVerificationRequestDTO;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class TwoFactorService implements ITwoFactorService {

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    private static final String KEY = "2fa:";
    private static final String EMAIL_KEY = "2fa:email:";

    private final UserRepository userRepository;
    private final IEmailService emailService;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public TwoFactorService(UserRepository userRepository, IEmailService emailService, StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public TwoFactorChallengeDTO createTwoFactorChallenge(String email, String deliveryMethod) throws AppObjectNotFoundException,
            AppObjectInvalidArgumentException {

        if (email == null || email.isBlank()) {
            throw new AppObjectInvalidArgumentException("TwoFactor", "Email is required for 2FA");
        }

        if (deliveryMethod == null || deliveryMethod.isBlank()) {
            throw new AppObjectInvalidArgumentException("TwoFactor", "Delivery method is required");
        }

        if (!"EMAIL".equalsIgnoreCase(deliveryMethod)) {
            throw new AppObjectInvalidArgumentException("TwoFactor", "Unsupported delivery method: " + deliveryMethod);
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppObjectNotFoundException(
                        "User", "User with email " + email + " not found"));

        String code = String.format("%06d", random.nextInt(1_000_000));
        String token = UUID.randomUUID().toString();

        TwoFactorRedisDTO entry = new TwoFactorRedisDTO(user.getEmail(), code);

        String key = KEY + token;
        String emailKey = EMAIL_KEY + user.getEmail();

        try {
            String json = objectMapper.writeValueAsString(entry);
            redisTemplate.opsForValue().set(key, json, DEFAULT_TTL);
            redisTemplate.opsForValue().set(emailKey, token, DEFAULT_TTL);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize TwoFactorRedisDTO", e);
            throw new AppObjectInvalidArgumentException("TwoFactor", "Failed to create two-factor challenge");
        }

        emailService.sendTwoFactorCode(user.getEmail(), code);

        log.info("2FA challenge created for user email={}, token={}, ttl={}s", user.getEmail(), token, DEFAULT_TTL.toSeconds());

        return new TwoFactorChallengeDTO(
                token,
                "EMAIL",
                "Two-factor code has been sent to your email."
        );
    }

    @Override
    public String verifyTwoFactorCode(TwoFactorVerificationRequestDTO request) throws AppObjectInvalidArgumentException {

        String token = request.twoFactorToken();
        String code = request.code();

        if (token == null || token.isBlank())
            throw new AppObjectInvalidArgumentException("TwoFactor", "Two-factor token is required");

        if (code == null || code.isBlank())
            throw new AppObjectInvalidArgumentException("TwoFactor", "Two-factor code is required");

        String key = KEY + token;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null)
            throw new AppObjectInvalidArgumentException("TwoFactor", "Invalid or expired two-factor token");

        TwoFactorRedisDTO entry;

        try {
            entry = objectMapper.readValue(json, TwoFactorRedisDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize TwoFactorRedisDTO for token {}", token, e);
            redisTemplate.delete(key);
            throw new AppObjectInvalidArgumentException("TwoFactor", "Invalid two-factor token");
        }

        String emailKey = EMAIL_KEY + entry.email();
        String latestTokenForEmail = redisTemplate.opsForValue().get(emailKey);

        if (latestTokenForEmail == null || !latestTokenForEmail.equals(token)) {
            throw new AppObjectInvalidArgumentException("TwoFactor", "Invalid two-factor token");
        }

        if (!entry.code().equals(code)) {
            throw new AppObjectInvalidArgumentException("TwoFactor", "Invalid two-factor code");
        }

        redisTemplate.delete(key);
        redisTemplate.delete(emailKey);

        log.info("2FA verification succeeded for email={} with token={}", entry.email(), token);

        return entry.email();
    }
}
