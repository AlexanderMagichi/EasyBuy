package com.teamchallenge.easybuy.shop.service.shopcontactinfo;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopContactInfo;
import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.shop.exception.ShopNotFoundException;
import com.teamchallenge.easybuy.shop.mapper.ShopContactInfoMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shopcontact.ShopContactInfoRepository;
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
 * Provides business operations for ShopContactInfoService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopContactInfoService {

    private final ShopContactInfoRepository contactInfoRepository;
    private final ShopRepository shopRepository;
    private final ShopContactInfoMapper mapper;
    private final ShopAccessGuard accessGuard;

    // ===================== READ =====================

    @Transactional(readOnly = true)
    public ShopContactInfoDTO getByShopId(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.debug("Fetching contact info for shop: {}", shopId);

        return contactInfoRepository.findByShop_ShopId(shopId)
                .map(mapper::toDto)
                .orElseThrow(() ->
                        new IllegalArgumentException("Contact info not found for shop: " + shopId));
    }

    @Transactional(readOnly = true)
    public ShopContactInfoDTO getActiveByShopId(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.debug("Fetching ACTIVE contact info for shop: {}", shopId);

        return contactInfoRepository.findActiveByShopId(shopId)
                .map(mapper::toDto)
                .orElseThrow(() ->
                        new IllegalArgumentException("Active contact info not found for shop: " + shopId));
    }

    // ===================== CREATE =====================

    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 500)
    )
    public ShopContactInfoDTO create(@NotNull UUID shopId,
                                     @Valid @NotNull ShopContactInfoDTO dto) {

        accessGuard.requireCanManageShop(shopId);

        log.info("Creating contact info for shop: {}", shopId);

        Shop shop = findShopOrThrow(shopId);

        if (contactInfoRepository.existsByShop_ShopIdAndActiveTrue(shopId)) {
            throw new IllegalStateException("Contact info already exists for shop: " + shopId);
        }

        ShopContactInfo entity = mapper.toEntity(dto);
        entity.setShop(shop);

        ShopContactInfo saved = contactInfoRepository.save(entity);

        log.info("Created contact info for shop: {}", shopId);
        return mapper.toDto(saved);
    }

    // ===================== UPDATE =====================

    public ShopContactInfoDTO update(@NotNull UUID shopId,
                                     @Valid @NotNull ShopContactInfoDTO dto) {

        accessGuard.requireCanManageShop(shopId);

        log.info("Updating contact info for shop: {}", shopId);

        ShopContactInfo entity = contactInfoRepository.findByShop_ShopId(shopId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Contact info not found for shop: " + shopId));

        mapper.updateEntityFromDto(dto, entity);

        ShopContactInfo updated = contactInfoRepository.save(entity);

        log.info("Updated contact info for shop: {}", shopId);
        return mapper.toDto(updated);
    }

    // ===================== PATCH =====================

    public ShopContactInfoDTO patch(@NotNull UUID shopId,
                                    @Valid @NotNull ShopContactInfoDTO dto) {

        log.info("Patching contact info for shop: {}", shopId);

        return update(shopId, dto);
    }

    // ===================== DELETE =====================

    public void deactivate(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.info("Deactivating contact info for shop: {}", shopId);

        ShopContactInfo entity = contactInfoRepository.findActiveByShopId(shopId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Active contact info not found for shop: " + shopId));

        entity.setActive(false);

        contactInfoRepository.save(entity);

        log.info("Deactivated contact info for shop: {}", shopId);
    }

    // ===================== VERIFY =====================

    public void verify(@NotNull UUID shopId) {
        accessGuard.requireAdmin();
        log.info("Verifying contact info for shop: {}", shopId);

        ShopContactInfo entity = contactInfoRepository.findByShop_ShopId(shopId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Contact info not found for shop: " + shopId));

        entity.markVerified();

        contactInfoRepository.save(entity);

        log.info("Verified contact info for shop: {}", shopId);
    }

    // ===================== HELPERS =====================

    private Shop findShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() ->
                        new ShopNotFoundException("Shop not found: " + shopId));
    }
}
