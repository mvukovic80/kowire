package com.minitwitter.backend.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {}
