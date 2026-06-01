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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Provides business operations for ShopSeoSettingsService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopSeoSettingsService {

    private static final String DEFAULT_OG_TYPE = "website";

    private final ShopSeoSettingsRepository seoRepository;
    private final ShopRepository shopRepository;
    private final ShopSeoSettingsMapper mapper;
    private final ShopAccessGuard accessGuard;

    @Value("${frontend.server.url}")
    private String frontendServerUrl;

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
        applyOgDefaults(shop, entity);
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
        entity.setShop(findShopOrThrow(shopId));
        applyOgDefaults(entity.getShop(), entity);
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

    private void applyOgDefaults(Shop shop, ShopSeoSettings entity) {
        if (shop == null || entity == null) {
            return;
        }

        String canonicalUrl = buildShopUrl(shop);
        if (!StringUtils.hasText(entity.getCanonicalUrl())) {
            entity.setCanonicalUrl(canonicalUrl);
        }

        if (!StringUtils.hasText(entity.getOgUrl())) {
            entity.setOgUrl(StringUtils.hasText(entity.getCanonicalUrl()) ? entity.getCanonicalUrl() : canonicalUrl);
        }

        if (!StringUtils.hasText(entity.getOgType())) {
            entity.setOgType(DEFAULT_OG_TYPE);
        }

        if (!StringUtils.hasText(entity.getOgSiteName())) {
            entity.setOgSiteName(shop.getShopName());
        }

        if (!StringUtils.hasText(entity.getOgLocale())) {
            entity.setOgLocale(normalizeLocale(shop.getLanguage()));
        }

        if (!StringUtils.hasText(entity.getOgTitle())) {
            entity.setOgTitle(entity.getDefaultMetaTitle());
        }

        if (!StringUtils.hasText(entity.getOgDescription())) {
            entity.setOgDescription(entity.getDefaultMetaDescription());
        }

        if (!StringUtils.hasText(entity.getOgImageAlt())) {
            entity.setOgImageAlt(shop.getShopName());
        }

        if (!StringUtils.hasText(entity.getOgImageSecureUrl()) && StringUtils.hasText(entity.getOgImageUrl())
                && entity.getOgImageUrl().startsWith("https://")) {
            entity.setOgImageSecureUrl(entity.getOgImageUrl());
        }

        if (!StringUtils.hasText(entity.getOgImageUrl()) && StringUtils.hasText(shop.getShopContactInfo() != null ? shop.getShopContactInfo().getLogoUrl() : null)) {
            entity.setOgImageUrl(shop.getShopContactInfo().getLogoUrl());
        }

        if (!StringUtils.hasText(entity.getOgImageSecureUrl()) && StringUtils.hasText(entity.getOgImageUrl())
                && entity.getOgImageUrl().startsWith("https://")) {
            entity.setOgImageSecureUrl(entity.getOgImageUrl());
        }
    }

    private String buildShopUrl(Shop shop) {
        String baseUrl = StringUtils.hasText(frontendServerUrl) ? frontendServerUrl : "";
        String slug = StringUtils.hasText(shop.getSlug()) ? shop.getSlug() : String.valueOf(shop.getShopId());
        return baseUrl + "/shops/" + slug;
    }

    private String normalizeLocale(String language) {
        if (!StringUtils.hasText(language)) {
            return "en_US";
        }

        String normalized = language.trim().toLowerCase();
        return switch (normalized) {
            case "uk" -> "uk_UA";
            case "en" -> "en_US";
            case "pl" -> "pl_PL";
            case "de" -> "de_DE";
            case "fr" -> "fr_FR";
            default -> normalized.replace('-', '_');
        };
    }
}

