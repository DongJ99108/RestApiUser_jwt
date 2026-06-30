package com.example.restapiuser.dto;

public record AuthResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserResponse user
) {
    public static AuthResponse bearer(String accessToken, String refreshToken, long expiresIn, UserResponse user) {
        return new AuthResponse("Bearer", accessToken, refreshToken, expiresIn, user);
    }
}
