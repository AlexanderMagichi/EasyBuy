package com.teamchallenge.easybuy.email.api.token;

import com.teamchallenge.easybuy.email.exception.TimeTokenException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Implementation of {@link TokenTimeExpirationCache} using Redis for distributed rate limiting.
 * Prevents excessive email sending by storing expiration timestamps in a shared Redis store.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisTokenTimeExpirationCache implements TokenTimeExpirationCache {

    private static final String KEY_PREFIX = "email:rate:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${temporary-cache.time.token}")
    private int expireTimeMinutes;

    /**
     * Logs the initialization of the Redis rate-limiting cache.
     */
    @PostConstruct
    void init() {
        log.info("token_expiration_cache.mode: Redis");
    }

    /**
     * Sets an expiration time in Redis for the given email to enforce rate limiting.
     *
     * @param email the email address to apply rate limiting to.
     */
    @Override
    public void manageEmailSendingRate(String email) {
        OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(expireTimeMinutes);
        redisTemplate.opsForValue().set(
                KEY_PREFIX + email,
                expiry.toString(),
                Duration.ofMinutes(expireTimeMinutes)
        );
    }

    /**
     * Checks if the email is currently rate-limited.
     *
     * @param email the email address to validate.
     * @throws TimeTokenException if the email is currently under a rate-limit restriction.
     */
    @Override
    public void validateTimeToken(String email) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + email);
        if (value != null) {
            throw new TimeTokenException(email, OffsetDateTime.parse(value));
        }
    }

    /**
     * Removes the rate-limiting record for the specified email.
     *
     * @param email the email address to clear from the cache.
     */
    @Override
    public void removeToken(String email) {
        redisTemplate.delete(KEY_PREFIX + email);
    }
}