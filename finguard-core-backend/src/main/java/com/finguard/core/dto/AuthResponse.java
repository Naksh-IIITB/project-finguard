package com.finguard.core.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String username,
        String email
) {
}
