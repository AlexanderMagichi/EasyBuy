package com.teamchallenge.easybuy.security.configuration;

import java.time.Duration;

public interface RateLimiter {
    RateLimitingConfiguration.RateLimitResult tryConsume(String key, int maxTokens, Duration windowDuration);
}
