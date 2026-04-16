package com.teamchallenge.easybuy.auth.service;

import com.teamchallenge.easybuy.auth.entity.Token;
import com.teamchallenge.easybuy.user.entity.User;
import com.teamchallenge.easybuy.auth.repository.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Provides business operations for TokenService.
 */
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenDurationMs;

    public void createToken(User user, String refreshToken) {
        tokenRepository.save(
                Token.builder()
                        .user(user)
                        .token(refreshToken)
                        .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                        .revoked(false)
                        .build()
        );
    }

    public boolean isValid(Token token) {
        return !token.isRevoked() && token.getExpiryDate().isAfter(Instant.now());
    }

    public void revokeToken(Token token) {
        token.setRevoked(true);
        tokenRepository.save(token);
    }

    @Transactional
    public void deleteAllTokensForUser(User user) {
        tokenRepository.deleteAllByUser(user);
    }

    public Token findByToken(String token) {
        return tokenRepository.findByToken(token).orElse(null);
    }

    @Transactional
    public void revokedAllTokensByUser(User user) {
        List<Token> tokens = tokenRepository.findAllByUserAndRevokedFalse(user);
        for (Token token : tokens) {
            token.setRevoked(true);
        }
        tokenRepository.saveAll(tokens);
    }
}
