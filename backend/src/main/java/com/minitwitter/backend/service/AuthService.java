package com.minitwitter.backend.service;

import com.minitwitter.backend.dto.AuthResponse;
import com.minitwitter.backend.dto.LoginRequest;
import com.minitwitter.backend.dto.RegisterRequest;
import com.minitwitter.backend.dto.UserResponse;
import com.minitwitter.backend.exception.ApiException;
import com.minitwitter.backend.model.User;
import com.minitwitter.backend.repository.UserRepository;
import com.minitwitter.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        String normalizedUsername = request.username().toLowerCase();
        String normalizedEmail = request.email().toLowerCase();

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw ApiException.conflict("Username is already taken");
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw ApiException.conflict("Email is already registered");
        }

        User user = User.builder()
                .username(normalizedUsername)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .displayName(request.displayName())
                .bio("")
                .createdAt(Instant.now())
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved.getId(), saved.getUsername());

        return new AuthResponse(token, UserResponse.from(saved));
    }

    public AuthResponse login(LoginRequest request) {
        String identifier = request.usernameOrEmail().toLowerCase();

        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> ApiException.unauthorized("Invalid username/email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw ApiException.unauthorized("Invalid username/email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new AuthResponse(token, UserResponse.from(user));
    }
}
