package com.teamchallenge.easybuy.security.jwt;

import com.teamchallenge.easybuy.security.configuration.JwtProperties;
import com.teamchallenge.easybuy.security.exception.JwtTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtSignKeyProvider jwtSignKeyProvider;
    private final JwtProperties jwtProperties;

    public String generateToken(final UserDetails userDetails) {
        return generateToken(Map.of(), userDetails);
    }

    public String generateToken(final Map<String, Object> extraClaims,
                               final UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtProperties.expiration(), jwtSignKeyProvider.get());
    }

    public String generateRefreshToken(final UserDetails userDetails) {
        return buildToken(Map.of(), userDetails, jwtProperties.refreshExpiration(), jwtSignKeyProvider.getRefresh());
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails,
                              Duration expiration, SecretKey key) {
        try {
            Instant now = Instant.now();
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuer(jwtProperties.issuer())
                    .setAudience(jwtProperties.audience())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(expiration)))
                    .signWith(key)
                    .compact();
        } catch (JwtException exception) {
            log.error("jwt.create.error: message={}", exception.getMessage(), exception);
            throw new JwtTokenException(exception);
        }
    }
}
