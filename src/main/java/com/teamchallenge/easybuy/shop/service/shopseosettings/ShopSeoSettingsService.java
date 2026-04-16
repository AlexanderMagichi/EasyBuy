package com.teamchallenge.easybuy.shop.service.shopseosettings;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopSeoSettings;
import com.teamchallenge.easybuy.shop.dto.ShopSeoSettingsDTO;
import com.teamchallenge.easybuy.shop.exception.ShopNotFoundException;
import com.teamchallenge.easybuy.shop.mapper.ShopSeoSettingsMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shopseosettings.ShopSeoSettingsRepository;
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

import java.util.UUID;

/**
 * Provides business operations for ShopSeoSettingsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopSeoSettingsService {

    private final ShopSeoSettingsRepository seoRepository;
    private final ShopRepository shopRepository;
    private final ShopSeoSettingsMapper mapper;
    private final ShopAccessGuard accessGuard;

    @Transactional(readOnly = true)
    public ShopSeoSettingsDTO getByShopId(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.debug("Fetching SEO settings for shop: {}", shopId);

        return seoRepository.findById(shopId)
                .map(mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("SEO settings not found for shop: " + shopId));
    }

    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            backoff = @Backoff(delay = 500)
    )
    public ShopSeoSettingsDTO create(@NotNull UUID shopId, @Valid @NotNull ShopSeoSettingsDTO dto) {
        accessGuard.requireCanManageShop(shopId);
        log.info("Creating SEO settings for shop: {}", shopId);

        Shop shop = findShopOrThrow(shopId);

        if (seoRepository.existsById(shopId)) {
            throw new IllegalStateException("SEO settings already exist for shop: " + shopId);
        }

        ShopSeoSettings entity = mapper.toEntity(dto);
        entity.setId(shopId);
        entity.setShop(shop);
        entity.calculateSeoScore();

        ShopSeoSettings saved = seoRepository.save(entity);

        log.info("Created SEO settings for shop: {}", shopId);
        return mapper.toDto(saved);
    }

    public ShopSeoSettingsDTO update(@NotNull UUID shopId, @Valid @NotNull ShopSeoSettingsDTO dto) {
        accessGuard.requireCanManageShop(shopId);
        log.info("Updating SEO settings for shop: {}", shopId);

        ShopSeoSettings entity = seoRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("SEO settings not found for shop: " + shopId));

        mapper.updateEntityFromDto(dto, entity);
        entity.calculateSeoScore();

        ShopSeoSettings updated = seoRepository.save(entity);

        log.info("Updated SEO settings for shop: {}", shopId);
        return mapper.toDto(updated);
    }

    public ShopSeoSettingsDTO patch(@NotNull UUID shopId, @Valid @NotNull ShopSeoSettingsDTO dto) {
        log.info("Patching SEO settings for shop: {}", shopId);
        return update(shopId, dto);
    }

    public void delete(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.info("Deleting SEO settings for shop: {}", shopId);

        if (!seoRepository.existsById(shopId)) {
            throw new IllegalArgumentException("SEO settings not found for shop: " + shopId);
        }

        seoRepository.deleteById(shopId);

        log.info("Deleted SEO settings for shop: {}", shopId);
    }

    private Shop findShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found: " + shopId));
    }
}

