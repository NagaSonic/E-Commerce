package com.example.security_learning.dto.response;

/**
 * Response containing the JWT access token and refresh token
 * issued after successful authentication.
 */
public class JwtLoginResponse {

    private String accessToken;
    private String refreshToken;

    public JwtLoginResponse() {
    }

    public JwtLoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}