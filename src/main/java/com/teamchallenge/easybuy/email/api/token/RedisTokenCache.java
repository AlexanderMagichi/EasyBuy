package com.teamchallenge.easybuy.email.api.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.email.exception.IncorrectTokenException;
import com.teamchallenge.easybuy.openapi.dto.UserRegistrationRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Implementation of {@link TokenCache} using Redis for distributed storage.
 * This ensures that registration tokens are shared across multiple service instances.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisTokenCache implements TokenCache {

    private static final String KEY_PREFIX = "email:token:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${temporary-cache.time.token}")
    private int expireTimeMinutes;

    /**
     * Logs the initialization of the Redis cache provider.
     */
    @PostConstruct
    void init() {
        log.info("token_cache.mode: Redis");
    }

    /**
     * Adds a registration request to the Redis cache with a TTL.
     *
     * @param tokenKey the unique key representing the token.
     * @param request  the user registration request data.
     * @throws IllegalStateException if serialization fails.
     */
    @Override
    public void addToken(String tokenKey, UserRegistrationRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            redisTemplate.opsForValue().set(
                    KEY_PREFIX + tokenKey,
                    json,
                    Duration.ofMinutes(expireTimeMinutes)
            );
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize registration request for token: {}", tokenKey, e);
            throw new IllegalStateException("Failed to serialize registration request", e);
        }
    }

    /**
     * Retrieves the registration request from Redis.
     *
     * @param tokenKey the unique key representing the token.
     * @return the {@link UserRegistrationRequest} if present.
     * @throws IncorrectTokenException if the token is missing or expired.
     * @throws IllegalStateException   if deserialization fails.
     */
    @Override
    public UserRegistrationRequest getToken(String tokenKey) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + tokenKey);
        if (json == null) {
            throw new IncorrectTokenException();
        }
        try {
            return objectMapper.readValue(json, UserRegistrationRequest.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize registration request for token: {}", tokenKey, e);
            throw new IllegalStateException("Failed to deserialize registration request", e);
        }
    }

    /**
     * Removes the registration request from Redis.
     *
     * @param tokenKey the unique key representing the token.
     */
    @Override
    public void removeToken(String tokenKey) {
        redisTemplate.delete(KEY_PREFIX + tokenKey);
    }
}