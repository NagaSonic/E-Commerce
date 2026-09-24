package com.example.security_learning.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload containing a refresh token.
 */
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}