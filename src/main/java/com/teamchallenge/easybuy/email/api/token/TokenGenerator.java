package com.teamchallenge.easybuy.email.api.token;

import com.teamchallenge.easybuy.email.exception.IncorrectTokenFormatException;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Component responsible for generating and validating numeric registration tokens.
 */
@Component
public class TokenGenerator {

    private static final int TOKEN_LENGTH = 9;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a new random numeric token.
     *
     * @return A numeric string representation of a random number with the configured length.
     */
    public String nextToken() {
        int bound = (int) Math.pow(10, TOKEN_LENGTH);
        return String.format("%0" + TOKEN_LENGTH + "d", RANDOM.nextInt(bound));
    }

    /**
     * Validates that the provided token matches the required format.
     *
     * @param token The token to validate.
     * @throws IncorrectTokenFormatException if the token is null, has invalid length, or contains non-digit characters.
     */
    public void tokenIsValid(String token) {
        if (token == null || token.length() != TOKEN_LENGTH || !token.chars().allMatch(Character::isDigit)) {
            throw new IncorrectTokenFormatException("#".repeat(TOKEN_LENGTH));
        }
    }
}