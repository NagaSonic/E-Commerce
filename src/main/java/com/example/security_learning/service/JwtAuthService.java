package com.example.security_learning.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.security_learning.dto.request.LoginRequest;
import com.example.security_learning.dto.response.JwtLoginResponse;
import com.example.security_learning.dto.response.RefreshTokenResponse;
import com.example.security_learning.dto.response.RefreshTokenResult;
import com.example.security_learning.entity.RefreshToken;

/**
 * Handles JWT authentication, refresh-token rotation,
 * and refresh-token revocation.
 */
@Service
public class JwtAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public JwtAuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Authenticates a user and issues an access token and refresh token.
     */
    public JwtLoginResponse jwtLogin(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUsername(),
                        request.getPassword()
                );

        Authentication authentication =
                authenticationManager.authenticate(authenticationRequest);

        String accessToken =
                jwtService.generateToken(authentication);

        RefreshTokenResult refreshTokenResult =
                refreshTokenService.createRefreshToken(
                        authentication.getName()
                );

        return new JwtLoginResponse(
                accessToken,
                refreshTokenResult.getRawToken()
        );
    }

    /**
     * Rotates the refresh token and issues a new access token.
     */
    @Transactional
    public RefreshTokenResponse refreshToken(String rawRefreshToken) {

        RefreshToken oldRefreshToken =
                refreshTokenService.validateRefreshToken(
                        rawRefreshToken
                );

        String accessToken =
                jwtService.generateTokenForUsername(
                        oldRefreshToken.getUsername()
                );

        RefreshTokenResult refreshTokenResult =
                refreshTokenService.createRefreshToken(
                        oldRefreshToken.getUsername(),
                        oldRefreshToken.getTokenFamilyId()
                );

        refreshTokenService.revokeRefreshToken(
                rawRefreshToken
        );

        return new RefreshTokenResponse(
                accessToken,
                refreshTokenResult.getRawToken()
        );
    }

    /**
     * Revokes a refresh token so it can no longer be used.
     */
    public void revokeRefreshToken(String rawRefreshToken) {

        refreshTokenService.revokeRefreshToken(
                rawRefreshToken
        );
    }
}