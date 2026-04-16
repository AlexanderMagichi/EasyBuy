package com.teamchallenge.easybuy.shop.service.shopanalytics;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopAnalytics;
import com.teamchallenge.easybuy.shop.dto.shopanalytics.ShopAnalyticsDTO;
import com.teamchallenge.easybuy.shop.exception.ShopNotFoundException;
import com.teamchallenge.easybuy.shop.mapper.ShopAnalyticsMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shopanalytics.ShopAnalyticsRepository;
import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Provides business operations for ShopAnalyticsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopAnalyticsService {

    private final ShopAnalyticsRepository analyticsRepository;
    private final ShopRepository shopRepository;
    private final ShopAnalyticsMapper mapper;
    private final ShopAccessGuard accessGuard;

    @Transactional(readOnly = true)
    public ShopAnalyticsDTO getByShopId(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.debug("Fetching analytics for shop: {}", shopId);
        return analyticsRepository.findById(shopId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Analytics not found for shop: " + shopId));
    }

    @Transactional(readOnly = true)
    public List<ShopAnalyticsDTO> getDeadShops() {
        accessGuard.requireAdmin();
        return analyticsRepository.findByDeadShopTrueOrderByInactiveDaysDesc()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            backoff = @Backoff(delay = 500)
    )
    public ShopAnalyticsDTO create(@NotNull UUID shopId, @Valid @NotNull ShopAnalyticsDTO dto) {
        accessGuard.requireAdmin();
        log.info("Creating analytics for shop: {}", shopId);

        if (analyticsRepository.existsById(shopId)) {
            throw new IllegalStateException("Analytics already exist for shop: " + shopId);
        }

        Shop shop = findShopOrThrow(shopId);
        ShopAnalytics entity = mapper.toEntity(dto);
        entity.setId(shopId);
        entity.setShop(shop);
        entity.recalculateDerivedMetrics();

        ShopAnalytics saved = analyticsRepository.save(entity);
        log.info("Created analytics for shop: {}", shopId);

        return mapper.toDto(saved);
    }

    public ShopAnalyticsDTO update(@NotNull UUID shopId, @Valid @NotNull ShopAnalyticsDTO dto) {
        accessGuard.requireAdmin();
        log.info("Updating analytics for shop: {}", shopId);

        ShopAnalytics entity = analyticsRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Analytics not found for shop: " + shopId));

        mapper.updateEntityFromDto(dto, entity);
        entity.recalculateDerivedMetrics();

        ShopAnalytics updated = analyticsRepository.save(entity);
        return mapper.toDto(updated);
    }

    public ShopAnalyticsDTO patch(@NotNull UUID shopId, @Valid @NotNull ShopAnalyticsDTO dto) {
        accessGuard.requireAdmin();
        log.info("Patching analytics for shop: {}", shopId);
        return update(shopId, dto);
    }

    public ShopAnalyticsDTO recalculate(@NotNull UUID shopId) {
        accessGuard.requireAdmin();
        log.info("Recalculating analytics for shop: {}", shopId);

        ShopAnalytics entity = analyticsRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Analytics not found for shop: " + shopId));

        entity.recalculateDerivedMetrics();
        return mapper.toDto(analyticsRepository.save(entity));
    }

    public void delete(@NotNull UUID shopId) {
        accessGuard.requireAdmin();
        log.info("Deleting analytics for shop: {}", shopId);

        if (!analyticsRepository.existsById(shopId)) {
            throw new IllegalArgumentException("Analytics not found for shop: " + shopId);
        }

        analyticsRepository.deleteById(shopId);
    }

    private Shop findShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found: " + shopId));
    }
}

