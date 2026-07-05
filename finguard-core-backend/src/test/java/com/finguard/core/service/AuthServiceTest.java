package com.finguard.core.service;

import com.finguard.core.dto.AuthResponse;
import com.finguard.core.dto.RegisterRequest;
import com.finguard.core.entity.User;
import com.finguard.core.exception.DuplicateResourceException;
import com.finguard.core.repository.UserRepository;
import com.finguard.core.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-for-step-five-auth-tests-123456", 60);
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService);
    }

    @Test
    void registerHashesPasswordAndReturnsToken() {
        RegisterRequest request = new RegisterRequest(
                "demo",
                "demo@finguard.local",
                "password123"
        );

        when(userRepository.existsByUsername("demo")).thenReturn(false);
        when(userRepository.existsByEmail("demo@finguard.local")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });
        AuthResponse response = authService.register(request);

        assertEquals("Bearer", response.tokenType());
        assertEquals(1L, response.userId());
        assertEquals("demo", response.username());
        assertEquals("demo@finguard.local", response.email());
        assertEquals("demo", jwtService.extractUsername(response.token()));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("hashed-password", userCaptor.getValue().getPasswordHash());
        assertNotEquals("password123", userCaptor.getValue().getPasswordHash());
    }

    @Test
    void registerFailsWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "demo",
                "demo@finguard.local",
                "password123"
        );

        when(userRepository.existsByUsername("demo")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
