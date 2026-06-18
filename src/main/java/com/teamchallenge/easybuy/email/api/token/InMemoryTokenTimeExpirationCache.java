package com.teamchallenge.easybuy.email.api.token;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.teamchallenge.easybuy.email.exception.TimeTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link TokenTimeExpirationCache} using Caffeine for in-memory storage.
 * Used for rate-limiting email sending frequency.
 */
@Slf4j
@Component
@ConditionalOnMissingBean(TokenTimeExpirationCache.class)
public class InMemoryTokenTimeExpirationCache implements TokenTimeExpirationCache {

    private final int expireTimeMinutes;
    private final Cache<String, OffsetDateTime> cache;

    /**
     * Constructs the cache with a defined expiration policy.
     *
     * @param expireTime expiration time in minutes for the rate-limiting window.
     */
    public InMemoryTokenTimeExpirationCache(@Value("${temporary-cache.time.token}") Integer expireTime) {
        log.info("token_expiration_cache.mode: in-memory (Caffeine implementation)");
        this.expireTimeMinutes = expireTime;
        this.cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(expireTime, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Records the email sending time to enforce rate limiting.
     *
     * @param email the email address to track.
     */
    @Override
    public void manageEmailSendingRate(String email) {
        cache.put(email, OffsetDateTime.now().plusMinutes(expireTimeMinutes));
    }

    /**
     * Validates if the email is currently rate-limited.
     *
     * @param email the email address to check.
     * @throws TimeTokenException if the email is still within the restricted time window.
     */
    @Override
    public void validateTimeToken(String email) {
        OffsetDateTime expiry = cache.getIfPresent(email);
        if (expiry != null) {
            throw new TimeTokenException(email, expiry);
        }
    }

    /**
     * Removes the rate-limiting entry for the specified email.
     *
     * @param email the email address to clear.
     */
    @Override
    public void removeToken(String email) {
        cache.invalidate(email);
    }
}