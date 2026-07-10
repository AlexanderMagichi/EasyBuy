package com.teamchallenge.easybuy.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teamchallenge.easybuy.infrastructure.config.CacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration for Redis cache management and RedisTemplate.
 * Configures serialization for both caching abstraction and direct Redis access.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {

    private final CacheProperties cacheProperties;

    /**
     * Creates a default ObjectMapper with JavaTimeModule support.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    /**
     * Configures the RedisCacheManager for use with Spring Cache abstraction (@Cacheable).
     * Provides typed serialization for complex objects.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("cache.mode: Redis");

        // Serializer for polymorphic types (needed for Cache abstraction)
        ObjectMapper typedMapper = new ObjectMapper();
        typedMapper.registerModule(new JavaTimeModule());
        typedMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE
        );
        GenericJackson2JsonRedisSerializer typedSerializer = GenericJackson2JsonRedisSerializer.builder()
                .objectMapper(typedMapper)
                .build();

        // Plain serializer for simple types
        ObjectMapper plainMapper = new ObjectMapper();
        plainMapper.registerModule(new JavaTimeModule());
        GenericJackson2JsonRedisSerializer plainSerializer = GenericJackson2JsonRedisSerializer.builder()
                .objectMapper(plainMapper)
                .build();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(cacheProperties.getDefaultTtl())
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(typedSerializer));

        RedisCacheConfiguration plainConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(plainSerializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("productById", defaultConfig.entryTtl(cacheProperties.getProductTtl()))
                .withCacheConfiguration("brands", plainConfig.entryTtl(cacheProperties.getBrandsTtl()))
                .withCacheConfiguration("sellers", plainConfig.entryTtl(cacheProperties.getSellersTtl()))
                .withCacheConfiguration("productImageUrl", defaultConfig.entryTtl(cacheProperties.getImageUrlTtl()))
                .build();
    }

    /**
     * Configures RedisTemplate for direct Redis operations.
     * Uses String for keys and JSON for values.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Set serializers for keys and hashes
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Set JSON serializer for values
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}