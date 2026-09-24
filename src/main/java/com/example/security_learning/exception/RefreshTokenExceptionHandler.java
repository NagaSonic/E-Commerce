package com.example.security_learning.exception;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles exceptions raised during refresh-token operations.
 */
@RestControllerAdvice
public class RefreshTokenExceptionHandler {

    /**
     * Returns HTTP 401 when a refresh token cannot be accepted.
     */
    @ExceptionHandler(RefreshTokenException.class)
    public void handleRefreshTokenException(
            RefreshTokenException exception,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write(
                "{\"error\":\"Invalid refresh token\",\"message\":\""
                        + exception.getMessage()
                        + "\"}"
        );
    }
}