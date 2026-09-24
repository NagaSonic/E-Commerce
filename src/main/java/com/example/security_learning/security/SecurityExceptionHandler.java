package com.example.security_learning.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles authentication and authorization failures and returns
 * JSON responses for HTTP 401 and 403 errors.
 */
public class SecurityExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {

    /**
     * Handles requests from unauthenticated users.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write(
                "{\"error\":\"Unauthorized\",\"message\":\"Please login first\"}"
        );
    }

    /**
     * Handles access-denied and CSRF failures.
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        response.setContentType("application/json");

        if (accessDeniedException instanceof CsrfException) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"error\":\"Forbidden\",\"message\":\"Invalid CSRF token\"}"
            );
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(
                "{\"error\":\"Forbidden\",\"message\":\"You do not have permission\"}"
        );
    }
}