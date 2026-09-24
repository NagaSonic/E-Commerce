package com.example.security_learning.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides endpoints used to verify Spring Security authentication,
 * authorization, permissions, and CSRF protection.
 */
@RestController
public class TestController {

    /**
     * Returns the username of the currently authenticated user.
     */
    @GetMapping("/user")
    public String user() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return "Hello " + authentication.getName();
    }

    /**
     * Verifies role-based method security.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Admin endpoint";
    }

    /**
     * Verifies permission-based method security.
     */
    @GetMapping("/users/read")
    @PreAuthorize("hasAuthority('USER_READ')")
    public String readUsers() {
        return "You have USER_READ permission";
    }

    /**
     * Returns the CSRF token for clients that need to submit protected requests.
     */
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }
}