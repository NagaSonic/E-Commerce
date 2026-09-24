package com.example.security_learning.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.security_learning.dto.request.ChangePasswordRequest;
import com.example.security_learning.dto.request.ForgotPasswordRequest;
import com.example.security_learning.dto.request.LoginRequest;
import com.example.security_learning.dto.request.RegisterRequest;
import com.example.security_learning.dto.request.ResetPasswordRequest;
import com.example.security_learning.dto.response.AuthResponse;
import com.example.security_learning.service.AuthService;

/**
 * Handles authentication and account-related operations.
 *
 * <p>This controller is responsible for user registration, session-based
 * login, password changes, and password reset operations.</p>
 */
@RestController
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final SecurityContextRepository securityContextRepository;
    private final RememberMeServices rememberMeServices;

    public AuthController(
            AuthService authService,
            AuthenticationManager authenticationManager,
            SessionAuthenticationStrategy sessionAuthenticationStrategy,
            SecurityContextRepository securityContextRepository,
            RememberMeServices rememberMeServices) {

        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
        this.securityContextRepository = securityContextRepository;
        this.rememberMeServices = rememberMeServices;
    }

    /**
     * Registers a new user with an encoded password and customer profile details.
     *
     * <p>New registrations are always assigned the default USER role and
     * USER_READ permission. Authorization values are controlled by the server
     * and are not accepted from the client.</p>
     *
     * @param request registration details
     * @return registration success response
     */
    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return new AuthResponse(
                "User registered successfully"
        );
    }

    /**
     * Authenticates a user and establishes a session-based security context.
     *
     * <p>The configured session authentication strategy is applied before
     * storing the authenticated user in the security context. Remember-me
     * authentication is enabled when requested by the client.</p>
     *
     * @param request login credentials
     * @param httpRequest current HTTP request
     * @param httpResponse current HTTP response
     * @return login success response
     */
    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUsername(),
                        request.getPassword()
                );

        Authentication authentication =
                authenticationManager.authenticate(authenticationRequest);

        sessionAuthenticationStrategy.onAuthentication(
                authentication,
                httpRequest,
                httpResponse
        );

        if ("true".equals(httpRequest.getParameter("remember-me"))) {

            rememberMeServices.loginSuccess(
                    httpRequest,
                    httpResponse,
                    authentication
            );
        }

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return new AuthResponse(
                "Login successful for: " + authentication.getName()
        );
    }

    /**
     * Changes the authenticated user's password.
     *
     * <p>The current password must be valid before the new password is saved.
     * All refresh tokens belonging to the user are revoked after a successful
     * password change.</p>
     *
     * @param request current and new password details
     * @param authentication authenticated user
     * @return password change success response
     */
    @PostMapping("/change-password")
    public AuthResponse changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        authService.changePassword(
                authentication.getName(),
                request
        );

        return new AuthResponse(
                "Password changed successfully"
        );
    }

    /**
     * Creates a temporary password reset token for the requested user.
     *
     * <p>The token is valid for 15 minutes.</p>
     *
     * @param request username requesting password recovery
     * @return password reset response
     */
    @PostMapping("/forgot-password")
    public AuthResponse forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        String resetToken = authService.forgotPassword(request);

        return new AuthResponse(
                "Password reset token: " + resetToken
        );
    }

    /**
     * Resets a user's password using a valid, non-expired reset token.
     *
     * @param request reset token and new password
     * @return password reset success response
     */
    @PostMapping("/reset-password")
    public AuthResponse resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);

        return new AuthResponse(
                "Password reset successfully"
        );
    }
}