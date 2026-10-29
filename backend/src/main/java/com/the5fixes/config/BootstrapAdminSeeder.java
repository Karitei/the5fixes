package com.the5fixes.config;

import com.the5fixes.user.Role;
import com.the5fixes.user.User;
import com.the5fixes.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Creates the first ADMIN account from environment variables — only when no admin
 * exists yet. Idempotent, and disabled entirely when the properties are blank.
 *
 * Usage: BOOTSTRAP_ADMIN_EMAIL=you@example.com BOOTSTRAP_ADMIN_PASSWORD=... mvn spring-boot:run
 * Then remove the variables.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BootstrapAdminSeeder {

    private final AppProperties appProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "app.bootstrap", name = "admin-email")
    public ApplicationRunner bootstrapAdminRunner() {
        return args -> {
            String email = appProperties.bootstrap().adminEmail();
            String password = appProperties.bootstrap().adminPassword();

            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                return;
            }
            if (userRepository.existsByEmailIgnoreCase(email)) {
                log.debug("Bootstrap admin {} already exists — skipping.", email);
                return;
            }

            User admin = new User(
                    email.trim().toLowerCase(),
                    passwordEncoder.encode(password),
                    appProperties.bootstrap().adminFullName(),
                    Role.ADMIN);
            userRepository.save(admin);
            log.warn("Bootstrap ADMIN account created for {}. Remove BOOTSTRAP_ADMIN_* env vars now.", email);
        };
    }
}
