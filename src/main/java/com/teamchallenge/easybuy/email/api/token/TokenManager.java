package com.teamchallenge.easybuy.email.api.token;

import com.teamchallenge.easybuy.openapi.dto.ConfirmEmailRequest;
import com.teamchallenge.easybuy.openapi.dto.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Manager component that orchestrates token generation, validation, and storage logic.
 * Acts as a facade for {@link TokenCache}, {@link TokenGenerator}, and {@link TokenTimeExpirationCache}.
 */
@Component
@RequiredArgsConstructor
public class TokenManager {

    private final TokenCache tokenCache;
    private final TokenGenerator tokenGenerator;
    private final TokenTimeExpirationCache tokenTimeExpirationCache;

    /**
     * Generates a new registration token, stores it in the cache, and manages the rate limiting.
     *
     * @param request The user registration data.
     * @return A newly generated token string.
     */
    public String generateToken(final UserRegistrationRequest request) {
        final String email = request.getEmail();

        // Ensure the user is not rate-limited before generating a new token
        tokenTimeExpirationCache.validateTimeToken(email);

        final String token = tokenGenerator.nextToken();

        // Store the registration request and set the rate limit
        tokenCache.addToken(token, request);
        tokenTimeExpirationCache.manageEmailSendingRate(email);

        return token;
    }

    /**
     * Validates the provided confirmation token and retrieves the associated registration request.
     *
     * @param confirmEmailRequest The request containing the token.
     * @return The cached {@link UserRegistrationRequest}.
     */
    public UserRegistrationRequest validateToken(final ConfirmEmailRequest confirmEmailRequest) {
        final String token = confirmEmailRequest.getToken();

        // Ensure the token format is valid
        tokenGenerator.tokenIsValid(token);

        return deleteTokenFromCache(token);
    }

    /**
     * Retrieves and removes the token and associated rate-limit entry from caches.
     *
     * @param token The token to remove.
     * @return The {@link UserRegistrationRequest} associated with the token.
     */
    public UserRegistrationRequest deleteTokenFromCache(String token) {
        UserRegistrationRequest userRegistrationRequest = tokenCache.getToken(token);

        // Remove token from cache
        tokenCache.removeToken(token);

        // Clear rate-limiting for the user's email
        tokenTimeExpirationCache.removeToken(userRegistrationRequest.getEmail());

        return userRegistrationRequest;
    }
}