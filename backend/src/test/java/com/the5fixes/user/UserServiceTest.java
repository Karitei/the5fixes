package com.the5fixes.user;

import com.the5fixes.common.ConflictException;
import com.the5fixes.common.NotFoundException;
import com.the5fixes.user.dto.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    void createNormalisesEmailAndHashesPassword() {
        when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("PlaintextPassword1!")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.create(new CreateUserRequest(
                "  Jane@Example.COM  ", "PlaintextPassword1!", "Jane Doe", Role.USER));

        assertThat(created.getEmail()).isEqualTo("jane@example.com");
        assertThat(created.getPasswordHash()).isEqualTo("$2a$10$hashed");
        assertThat(created.getFullName()).isEqualTo("Jane Doe");
        verify(passwordEncoder).encode("PlaintextPassword1!");
    }

    @Test
    void createRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(new CreateUserRequest(
                "taken@example.com", "PlaintextPassword1!", "Taken", Role.USER)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void getByIdThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void loadUserByUsernameMapsRoleToAuthority() {
        User user = new User("admin@example.com", "hash", "Admin", Role.ADMIN);
        when(userRepository.findByEmailIgnoreCase("admin@example.com")).thenReturn(Optional.of(user));

        var details = userService.loadUserByUsername("admin@example.com");

        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }
}
