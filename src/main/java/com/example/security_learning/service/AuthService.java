package com.example.security_learning.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security_learning.dto.request.ChangePasswordRequest;
import com.example.security_learning.dto.request.ForgotPasswordRequest;
import com.example.security_learning.dto.request.RegisterRequest;
import com.example.security_learning.dto.request.ResetPasswordRequest;
import com.example.security_learning.entity.User;
import com.example.security_learning.repository.UserRepository;

/**
 * Handles user registration and account password operations.
 */
@Service
public class AuthService {

    private static final long PASSWORD_RESET_VALIDITY_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Registers a new user with the default USER role and USER_READ permission.
     *
     * @param request registration details
     */
    public void register(RegisterRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole("USER");
        user.setPermissions("USER_READ");

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        userRepository.save(user);
    }

    /**
     * Changes the authenticated user's password.
     *
     * @param username authenticated username
     * @param request current and new password details
     */
    public void changePassword(
            String username,
            ChangePasswordRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        refreshTokenService.revokeAllRefreshTokensForUser(username);
    }

    /**
     * Creates a temporary password reset token for a user.
     *
     * @param request username requesting password recovery
     * @return generated password reset token
     */
    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString();

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(
                LocalDateTime.now()
                        .plusMinutes(PASSWORD_RESET_VALIDITY_MINUTES)
        );

        userRepository.save(user);

        return resetToken;
    }

    /**
     * Resets a user's password using a valid reset token.
     *
     * @param request reset token and new password
     */
    public void resetPassword(ResetPasswordRequest request) {

        if (request.getToken() == null
                || request.getToken().isBlank()) {

            throw new RuntimeException(
                    "Reset token is required"
            );
        }

        User user = userRepository
                .findByResetToken(request.getToken())
                .orElseThrow(() ->
                        new RuntimeException("Invalid reset token"));

        if (user.getResetTokenExpiry() == null
                || user.getResetTokenExpiry()
                        .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Reset token has expired"
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }
}