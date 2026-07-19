package com.teamchallenge.easybuy.email.api.token;

import com.teamchallenge.easybuy.openapi.dto.UserRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TokenCacheConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
    public TokenCache redisTokenCache(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        return new RedisTokenCache(redisTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(TokenCache.class)
    public TokenCache inMemoryTokenCache(@Value("${temporary-cache.time.token}") Integer expireTime) {
        return new InMemoryTokenCache(expireTime);
    }
}
