package com.the5fixes.auth;

import com.the5fixes.common.BadRequestException;
import com.the5fixes.common.ConflictException;
import com.the5fixes.config.AppProperties;
import com.the5fixes.user.Role;
import com.the5fixes.user.User;
import com.the5fixes.user.UserRepository;
import com.the5fixes.user.dto.UserResponse;
import com.the5fixes.auth.dto.AuthResponse;
import com.the5fixes.auth.dto.LoginRequest;
import com.the5fixes.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppProperties appProperties;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = userRepository.save(new User(
                email,
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                Role.USER));

        log.info("New user registered: {}", email);
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Delegates password checking to Spring Security (BCrypt + timing-safe compare).
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BadRequestException("Account no longer exists"));

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken stored = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (!stored.isActive()) {
            // Reuse of a revoked/expired token is a compromise signal: kill every session.
            refreshTokenRepository.revokeAllForUser(stored.getUser().getId());
            log.warn("Revoked refresh token reused for user {} — all sessions terminated",
                    stored.getUser().getId());
            throw new BadRequestException("Refresh token is no longer valid. Please sign in again.");
        }

        // Rotation: the old token dies, a new one is issued.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);
        return issueTokens(stored.getUser());
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    private AuthResponse issueTokens(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = createRefreshToken(user);
        return AuthResponse.of(accessToken, refreshToken, jwtService.accessTokenTtlSeconds(),
                UserResponse.from(user));
    }

    private String createRefreshToken(User user) {
        String value = UUID.randomUUID() + "." + UUID.randomUUID();
        Instant expiry = Instant.now().plus(appProperties.jwt().refreshTokenTtl());
        refreshTokenRepository.save(new RefreshToken(value, user, expiry));
        return value;
    }
}
