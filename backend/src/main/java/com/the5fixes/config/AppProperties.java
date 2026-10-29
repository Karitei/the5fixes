package com.the5fixes.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

/**
 * Typed application configuration. Fail-fast at startup if a value is missing —
 * better than discovering it in production.
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Cors cors, Bootstrap bootstrap) {

    public record Jwt(
            @NotBlank String secret,
            Duration accessTokenTtl,
            Duration refreshTokenTtl,
            String issuer
    ) {}

    public record Cors(List<String> allowedOrigins) {}

    /**
     * Optional first-admin creation on startup. Leave blank in production once
     * you have a real admin — a hardcoded bootstrap credential is a backdoor.
     */
    public record Bootstrap(String adminEmail, String adminPassword, String adminFullName) {}
}
