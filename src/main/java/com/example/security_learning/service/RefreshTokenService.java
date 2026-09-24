package com.example.security_learning.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.security_learning.dto.response.RefreshTokenResult;
import com.example.security_learning.entity.RefreshToken;
import com.example.security_learning.exception.RefreshTokenException;
import com.example.security_learning.repository.RefreshTokenRepository;

/**
 * Handles refresh-token creation, validation, rotation, and revocation.
 *
 * <p>Only hashed refresh tokens are stored in the database. Token families
 * are used to detect refresh-token reuse and revoke related tokens when
 * reuse is detected.</p>
 */
@Service
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Creates a new refresh token and starts a new token family.
     *
     * @param username username associated with the refresh token
     * @return persisted refresh token and its raw value
     */
    public RefreshTokenResult createRefreshToken(String username) {
        String rawToken = UUID.randomUUID().toString();
        String tokenFamilyId = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(hashToken(rawToken));
        refreshToken.setUsername(username);
        refreshToken.setTokenFamilyId(tokenFamilyId);
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS)
        );
        refreshToken.setRevoked(false);

        RefreshToken savedToken =
                refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResult(savedToken, rawToken);
    }

    /**
     * Creates a new refresh token within an existing token family.
     *
     * @param username username associated with the refresh token
     * @param tokenFamilyId existing token family identifier
     * @return persisted refresh token and its raw value
     */
    public RefreshTokenResult createRefreshToken(
            String username,
            String tokenFamilyId) {

        String rawToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(hashToken(rawToken));
        refreshToken.setUsername(username);
        refreshToken.setTokenFamilyId(tokenFamilyId);
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS)
        );
        refreshToken.setRevoked(false);

        RefreshToken savedToken =
                refreshTokenRepository.save(refreshToken);

        return new RefreshTokenResult(savedToken, rawToken);
    }

    /**
     * Validates a refresh token.
     *
     * <p>A revoked token is treated as possible token reuse and causes
     * the entire token family to be revoked.</p>
     *
     * @param token raw refresh token supplied by the client
     * @return the persisted refresh token
     * @throws RefreshTokenException if the token is invalid, revoked,
     *         reused, or expired
     */
    public RefreshToken validateRefreshToken(String token) {
        String hashedToken = hashToken(token);

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(hashedToken)
                        .orElseThrow(() ->
                                new RefreshTokenException(
                                        "Invalid refresh token"
                                ));

        if (refreshToken.isRevoked()) {
            revokeTokenFamily(refreshToken.getTokenFamilyId());

            throw new RefreshTokenException(
                    "Refresh token reuse detected"
            );
        }

        if (refreshToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RefreshTokenException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    /**
     * Revokes a single refresh token.
     *
     * @param token raw refresh token
     * @throws RefreshTokenException if the token does not exist
     */
    public void revokeRefreshToken(String token) {
        String hashedToken = hashToken(token);

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(hashedToken)
                        .orElseThrow(() ->
                                new RefreshTokenException(
                                        "Invalid refresh token"
                                ));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Hashes a refresh token using SHA-256 before database storage
     * or lookup.
     *
     * @param token raw refresh token
     * @return hexadecimal SHA-256 hash
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not hash refresh token",
                    e
            );
        }
    }

    /**
     * Revokes every refresh token belonging to a token family.
     *
     * @param tokenFamilyId token family identifier
     */
    public void revokeTokenFamily(String tokenFamilyId) {
        List<RefreshToken> tokens =
                refreshTokenRepository
                        .findAllByTokenFamilyId(tokenFamilyId);

        for (RefreshToken token : tokens) {
            token.setRevoked(true);
        }

        refreshTokenRepository.saveAllAndFlush(tokens);
    }

    /**
     * Revokes all refresh tokens belonging to a user.
     *
     * @param username username whose refresh tokens should be revoked
     */
    public void revokeAllRefreshTokensForUser(String username) {
        List<RefreshToken> tokens =
                refreshTokenRepository.findAllByUsername(username);

        for (RefreshToken token : tokens) {
            token.setRevoked(true);
        }

        refreshTokenRepository.saveAllAndFlush(tokens);
    }
}