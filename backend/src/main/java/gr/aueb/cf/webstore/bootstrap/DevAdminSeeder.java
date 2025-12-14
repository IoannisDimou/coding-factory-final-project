package gr.aueb.cf.webstore.bootstrap;

import gr.aueb.cf.webstore.core.enums.Role;
import gr.aueb.cf.webstore.model.User;
import gr.aueb.cf.webstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "docker"})
public class DevAdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DevAdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${BOOTSTRAP_ADMIN_EMAIL:admin@local.test}")
    private String email;

    @Value("${BOOTSTRAP_ADMIN_PASSWORD:Admin1234!?.}")
    private String password;

    @Value("${BOOTSTRAP_ADMIN_FIRSTNAME:admin}")
    private String firstname;

    @Value("${BOOTSTRAP_ADMIN_LASTNAME:admin}")
    private String lastname;

    @Override
    public void run(String... args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) return;

        if (userRepository.findByEmail(email).isPresent()) return;

        User admin = new User();
        admin.setEmailVerified(true);
        admin.setIsActive(true);
        admin.setFirstname(firstname);
        admin.setLastname(lastname);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);
    }

}
