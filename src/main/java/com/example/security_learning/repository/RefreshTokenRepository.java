package com.example.security_learning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security_learning.entity.RefreshToken;

/**
 * Repository for persistence operations on refresh tokens.
 */
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByTokenFamilyId(String tokenFamilyId);

    List<RefreshToken> findAllByUsername(String username);

    void deleteByUsername(String username);
}