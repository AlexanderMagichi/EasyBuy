package com.teamchallenge.easybuy.security.jwt;

import com.teamchallenge.easybuy.security.exception.JwtTokenHasNoUserEmailException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class JwtClaimExtractor {

    private final JwtParser jwtParser;

    public JwtClaimExtractor(JwtSignKeyProvider jwtSignKeyProvider) {
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(jwtSignKeyProvider.get())
                .build();
    }

    public String extractEmail(final String jwtToken) {
        try {
            return Optional.ofNullable(extractAllClaims(jwtToken).getSubject())
                    .filter(StringUtils::hasText)
                    .orElseThrow(() -> new JwtTokenHasNoUserEmailException("Missing email in JWT token"));
        } catch (JwtTokenHasNoUserEmailException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new JwtTokenHasNoUserEmailException("Failed to extract email from JWT token", ex);
        }
    }

    private Claims extractAllClaims(final String jwtToken) {
        return jwtParser.parseClaimsJws(jwtToken).getBody();
    }
}
