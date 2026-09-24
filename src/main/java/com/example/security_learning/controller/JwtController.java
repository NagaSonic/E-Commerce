package com.example.security_learning.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.security_learning.dto.request.LoginRequest;
import com.example.security_learning.dto.request.RefreshTokenRequest;
import com.example.security_learning.dto.response.AuthResponse;
import com.example.security_learning.dto.response.JwtLoginResponse;
import com.example.security_learning.dto.response.RefreshTokenResponse;
import com.example.security_learning.service.JwtAuthService;
import jakarta.validation.Valid;

/**
 * Handles HTTP endpoints for JWT authentication
 * and refresh-token operations.
 */
@RestController
public class JwtController {

    private final JwtAuthService jwtAuthService;

    public JwtController(JwtAuthService jwtAuthService) {
        this.jwtAuthService = jwtAuthService;
    }

    /**
     * Authenticates a user and issues JWT access and refresh tokens.
     */
    
    @PostMapping("/jwt-login")
    public JwtLoginResponse jwtLogin(@Valid @RequestBody LoginRequest request) {
        return jwtAuthService.jwtLogin(request);
    }

    /**
     * Rotates the refresh token and issues a new access token.
     */
    @PostMapping("/refresh-token")
    		public RefreshTokenResponse refreshToken(
    		        @Valid @RequestBody RefreshTokenRequest request) {

        return jwtAuthService.refreshToken(
                request.getRefreshToken()
        );
    }

    /**
     * Revokes a refresh token.
     */
    @PostMapping("/revoke-refresh-token")
    public AuthResponse revokeRefreshToken(
            @Valid @RequestBody RefreshTokenRequest request){

        jwtAuthService.revokeRefreshToken(
                request.getRefreshToken()
        );

        return new AuthResponse("Refresh token revoked");
    }
}