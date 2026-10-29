package com.the5fixes.user;

import com.the5fixes.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * The authenticated user's own profile.
     * The JWT subject is the user id (see JwtService), so no email lookup is needed —
     * and no extra query is required to resolve the identity.
     */
    @GetMapping
    public UserResponse me(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return UserResponse.from(userService.getById(userId));
    }
}
