package com.the5fixes.user;

import com.the5fixes.auth.UserPrincipal;
import com.the5fixes.common.ConflictException;
import com.the5fixes.common.NotFoundException;
import com.the5fixes.common.PageResponse;
import com.the5fixes.user.dto.CreateUserRequest;
import com.the5fixes.user.dto.UpdateUserRequest;
import com.the5fixes.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(email)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));
    }

    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }

    @Transactional
    public User create(CreateUserRequest request) {
        String email = normalise(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        return userRepository.save(new User(
                email,
                passwordEncoder.encode(request.password()),
                request.fullName().trim(),
                request.role()));
    }

    @Transactional
    public User update(UUID id, UpdateUserRequest request) {
        User user = getById(id);
        user.setFullName(request.fullName().trim());
        user.setRole(request.role());
        user.setEnabled(request.enabled());
        return userRepository.save(user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> search(String query, Pageable pageable) {
        String normalised = (query == null || query.isBlank()) ? null : query.trim();
        Page<User> page = userRepository.search(normalised, pageable);
        return PageResponse.from(page, UserResponse::from);
    }

    private String normalise(String email) {
        return email.trim().toLowerCase();
    }
}
