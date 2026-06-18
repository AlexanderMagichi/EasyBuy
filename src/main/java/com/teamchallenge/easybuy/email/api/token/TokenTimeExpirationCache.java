package com.teamchallenge.easybuy.email.api.token;

/**
 * Interface for managing email sending rate limits.
 * Provides abstraction for both in-memory (Caffeine) and distributed (Redis) implementations.
 */
public interface TokenTimeExpirationCache {

    /**
     * Records the email sending time to enforce rate limiting.
     *
     * @param email The email address to apply rate limiting to.
     */
    void manageEmailSendingRate(String email);

    /**
     * Validates if the email is currently rate-limited.
     *
     * @param email The email address to validate.
     * @throws com.teamchallenge.easybuy.email.exception.TimeTokenException if the email is currently restricted.
     */
    void validateTimeToken(String email);

    /**
     * Removes the rate-limiting entry for the specified email.
     *
     * @param email The email address to clear from the cache.
     */
    void removeToken(String email);
}