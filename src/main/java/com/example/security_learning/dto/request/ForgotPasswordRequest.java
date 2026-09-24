package com.example.security_learning.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload used to initiate a password reset.
 */
public class ForgotPasswordRequest {

    @NotBlank(message = "Username is required")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}