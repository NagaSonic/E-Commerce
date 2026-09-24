package com.example.security_learning.exception;

/**
 * Indicates that a refresh token is invalid, expired, revoked,
 * or has been detected as reused.
 */
public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException(String message) {
        super(message);
    }
}