package com.finguard.core.service;

import com.finguard.core.dto.AuthResponse;
import com.finguard.core.dto.LoginRequest;
import com.finguard.core.dto.RegisterRequest;
import com.finguard.core.entity.User;
import com.finguard.core.exception.DuplicateResourceException;
import com.finguard.core.repository.UserRepository;
import com.finguard.core.security.JwtService;
import com.finguard.core.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already exists: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);
        UserPrincipal principal = UserPrincipal.from(savedUser);
        String token = jwtService.generateToken(principal);

        return toAuthResponse(token, principal);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password())
        );

        User user = userRepository.findByUsername(request.usernameOrEmail())
                .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
                .orElseThrow();

        UserPrincipal principal = UserPrincipal.from(user);
        String token = jwtService.generateToken(principal);

        return toAuthResponse(token, principal);
    }

    private AuthResponse toAuthResponse(String token, UserPrincipal principal) {
        return new AuthResponse(
                token,
                "Bearer",
                principal.getId(),
                principal.getUsername(),
                principal.getEmail()
        );
    }
}
