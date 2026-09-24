package com.example.security_learning.dto.response;

import com.example.security_learning.entity.RefreshToken;

/**
 * Internal result containing the persisted refresh token and
 * its raw value returned to the authentication flow.
 */
public class RefreshTokenResult {

    private final RefreshToken refreshToken;
    private final String rawToken;

    public RefreshTokenResult(
            RefreshToken refreshToken,
            String rawToken) {

        this.refreshToken = refreshToken;
        this.rawToken = rawToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public String getRawToken() {
        return rawToken;
    }
}