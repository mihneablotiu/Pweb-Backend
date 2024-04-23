package com.pweb.springBackend;

import com.pweb.springBackend.entities.User;
import com.pweb.springBackend.enums.UserRole;
import com.pweb.springBackend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Add a default admin user to the db
        if (!userRepository.existsByEmail("admin@admin.com")) {
            User user = new User();
            user.setUsername("admin");
            user.setPasswordHash(passwordEncoder.encode("admin"));
            user.setEmail("admin@admin.com");
            user.setRole(UserRole.ADMIN);

            userRepository.save(user);
        }
    }
}
