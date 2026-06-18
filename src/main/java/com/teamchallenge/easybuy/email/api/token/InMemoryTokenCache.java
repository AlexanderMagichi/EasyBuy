package com.teamchallenge.easybuy.email.api.token;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.teamchallenge.easybuy.email.exception.IncorrectTokenException;
import com.teamchallenge.easybuy.openapi.dto.UserRegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link TokenCache} using Caffeine for in-memory storage.
 * Provides a bounded cache with expiration policy.
 */
@Slf4j
@Component
@ConditionalOnMissingBean(TokenCache.class)
public class InMemoryTokenCache implements TokenCache {

    private final Cache<String, UserRegistrationRequest> cache;

    /**
     * Constructs the cache with a specified expiration time.
     *
     * @param expireTime expiration time in minutes for the cache entries.
     */
    public InMemoryTokenCache(@Value("${temporary-cache.time.token}") Integer expireTime) {
        log.info("token_cache.mode: in-memory (Caffeine implementation)");
        this.cache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(expireTime, TimeUnit.MINUTES)
                .build();
    }

    /**
     * Adds a registration request to the cache.
     *
     * @param tokenKey the unique key representing the token.
     * @param request  the user registration request data.
     */
    @Override
    public void addToken(String tokenKey, UserRegistrationRequest request) {
        cache.put(tokenKey, request);
    }

    /**
     * Retrieves the registration request associated with the token.
     *
     * @param tokenKey the unique key representing the token.
     * @return the {@link UserRegistrationRequest} if present.
     * @throws IncorrectTokenException if the token is invalid or expired.
     */
    @Override
    public UserRegistrationRequest getToken(String tokenKey) {
        UserRegistrationRequest result = cache.getIfPresent(tokenKey);
        if (result == null) {
            throw new IncorrectTokenException();
        }
        return result;
    }

    /**
     * Removes the registration request associated with the token from the cache.
     *
     * @param tokenKey the unique key representing the token.
     */
    @Override
    public void removeToken(String tokenKey) {
        cache.invalidate(tokenKey);
    }
}