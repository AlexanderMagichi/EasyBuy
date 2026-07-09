package com.teamchallenge.easybuy.shop.service.analytics;


import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Analytics Service - analytics collection
 * *
 * * Features:
 * * - Store metrics
 * * - View counters
 * * - Creation/update statistics
 * * - Redis caching of metrics
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ShopRepository shopRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ANALYTICS_PREFIX = "analytics:";
    private static final String SHOP_VIEWS_KEY = ANALYTICS_PREFIX + "shop:views:";
    private static final String SHOP_CREATED_KEY = ANALYTICS_PREFIX + "shop:created:";
    private static final String DAILY_STATS_KEY = ANALYTICS_PREFIX + "daily:";

    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> recordShopCreation(Shop shop) {
        try {
            log.info("Recording shop creation analytics for: {}", shop.getShopName());

            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dailyKey = DAILY_STATS_KEY + today;

            // Increase the counter of stores created per day
            redisTemplate.opsForHash().increment(dailyKey, "shops_created", 1);
            redisTemplate.expire(dailyKey, 90, TimeUnit.DAYS); // Store for 90 days

            // We record information about a specific store
            String shopCreatedKey = SHOP_CREATED_KEY + shop.getShopId();
            redisTemplate.opsForHash().put(shopCreatedKey, "created_at", shop.getCreatedAt().toString());
            redisTemplate.opsForHash().put(shopCreatedKey, "seller_id", shop.getSeller().getId().toString());
            redisTemplate.opsForHash().put(shopCreatedKey, "shop_type", shop.getShopType().toString());
            redisTemplate.expire(shopCreatedKey, 365, TimeUnit.DAYS); // Store for 365 days

            // Statistics by store type
            redisTemplate.opsForHash().increment(dailyKey, "shops_" + shop.getShopType().name().toLowerCase(), 1);

            log.debug("Shop creation analytics recorded for: {}", shop.getShopName());
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to record shop creation analytics for: {}", shop.getShopName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> recordShopUpdate(Shop shop) {
        try {
            log.info("Recording shop update analytics for: {}", shop.getShopName());

            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dailyKey = DAILY_STATS_KEY + today;

            // We're increasing the daily store update counter.
            redisTemplate.opsForHash().increment(dailyKey, "shops_updated", 1);
            redisTemplate.expire(dailyKey, 90, TimeUnit.DAYS);

            // We record the time of the last update
            String shopKey = SHOP_CREATED_KEY + shop.getShopId();
            redisTemplate.opsForHash().put(shopKey, "last_updated", LocalDateTime.now().toString());

            log.debug("Shop update analytics recorded for: {}", shop.getShopName());
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to record shop update analytics for: {}", shop.getShopName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> recordShopDeletion(Shop shop) {
        try {
            log.info("Recording shop deletion analytics for: {}", shop.getShopName());

            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dailyKey = DAILY_STATS_KEY + today;

            // We're increasing the counter of stores removed per day.
            redisTemplate.opsForHash().increment(dailyKey, "shops_deleted", 1);
            redisTemplate.expire(dailyKey, 90, TimeUnit.DAYS);

            // Marking the store as deleted
            String shopKey = SHOP_CREATED_KEY + shop.getShopId();
            redisTemplate.opsForHash().put(shopKey, "deleted_at", LocalDateTime.now().toString());

            log.debug("Shop deletion analytics recorded for: {}", shop.getShopName());
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to record shop deletion analytics for: {}", shop.getShopName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("analyticsTaskExecutor")
    public CompletableFuture<Void> incrementShopViews(UUID shopId) {
        try {
            log.debug("Incrementing view count for shop: {}", shopId);

            String viewsKey = SHOP_VIEWS_KEY + shopId;
            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

            // Total views counter
            redisTemplate.opsForValue().increment(viewsKey);
            redisTemplate.expire(viewsKey, 365, TimeUnit.DAYS);

            // Views by day
            String dailyViewsKey = viewsKey + ":" + today;
            redisTemplate.opsForValue().increment(dailyViewsKey);
            redisTemplate.expire(dailyViewsKey, 90, TimeUnit.DAYS);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to increment view count for shop: {}", shopId, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // === SYNCHRONOUS READ METHODS ===

    public Long getShopViews(UUID shopId) {
        try {
            String viewsKey = SHOP_VIEWS_KEY + shopId;
            String viewsStr = (String) redisTemplate.opsForValue().get(viewsKey);
            return viewsStr != null ? Long.parseLong(viewsStr) : 0L;
        } catch (Exception e) {
            log.warn("Failed to get shop views for: {}", shopId, e);
            return 0L;
        }
    }

    public Long getTodayShopsCreated() {
        try {
            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dailyKey = DAILY_STATS_KEY + today;
            Object count = redisTemplate.opsForHash().get(dailyKey, "shops_created");
            return count != null ? Long.parseLong(count.toString()) : 0L;
        } catch (Exception e) {
            log.warn("Failed to get today's shop creation count", e);
            return 0L;
        }
    }

    public Long getTodayShopsUpdated() {
        try {
            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String dailyKey = DAILY_STATS_KEY + today;
            Object count = redisTemplate.opsForHash().get(dailyKey, "shops_updated");
            return count != null ? Long.parseLong(count.toString()) : 0L;
        } catch (Exception e) {
            log.warn("Failed to get today's shop update count", e);
            return 0L;
        }
    }

    public void recordError(String operation, Exception error) {
        try {
            log.warn("Recording error for operation: {}", operation, error);

            String today = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String errorKey = ANALYTICS_PREFIX + "errors:" + today;

            redisTemplate.opsForHash().increment(errorKey, operation + "_errors", 1);
            redisTemplate.expire(errorKey, 30, TimeUnit.DAYS);

        } catch (Exception e) {
            log.error("Failed to record error analytics", e);
        }
    }
}