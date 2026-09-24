package com.example.security_learning.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.example.security_learning.entity.User;
import com.example.security_learning.repository.UserRepository;

/**
 * Generates JWT access tokens for authenticated users.
 *
 * <p>Tokens contain the authenticated username, role, permissions,
 * issue time, and expiration time.</p>
 */
@Service
public class JwtService {

    private static final long ACCESS_TOKEN_VALIDITY_MINUTES = 15;

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    public JwtService(
            UserRepository userRepository,
            @Value("${jwt.secret}") String secret) {

        this.userRepository = userRepository;

        SecretKey secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        this.jwtEncoder = NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Generates an access token using authorities from the current
     * authenticated user.
     *
     * @param authentication the authenticated user's security context
     * @return signed JWT access token
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        String role = authentication.getAuthorities()
                .stream()
                .filter(authority ->
                        authority.getAuthority().startsWith("ROLE_"))
                .map(authority ->
                        authority.getAuthority().substring(5))
                .findFirst()
                .orElse("USER");

        List<String> permissions = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority ->
                        !authority.startsWith("ROLE_"))
                .filter(authority ->
                        !authority.startsWith("FACTOR_"))
                .toList();

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(
                        now.plus(
                                ACCESS_TOKEN_VALIDITY_MINUTES,
                                ChronoUnit.MINUTES
                        )
                )
                .claim("role", role)
                .claim("permissions", permissions)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    /**
     * Generates an access token for a username by loading the user's
     * current role and permissions from the database.
     *
     * @param username the username for which the token is generated
     * @return signed JWT access token
     * @throws RuntimeException if the user does not exist
     */
    public String generateTokenForUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Instant now = Instant.now();

        List<String> permissions = List.of();

        if (user.getPermissions() != null
                && !user.getPermissions().isBlank()) {

            permissions = Arrays.stream(
                            user.getPermissions().split(","))
                    .map(String::trim)
                    .filter(permission -> !permission.isBlank())
                    .toList();
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(
                        now.plus(
                                ACCESS_TOKEN_VALIDITY_MINUTES,
                                ChronoUnit.MINUTES
                        )
                )
                .claim("role", user.getRole())
                .claim("permissions", permissions)
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}