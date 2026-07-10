package com.teamchallenge.easybuy.shop.service;

import com.teamchallenge.easybuy.shop.event.ShopCreatedEvent;
import com.teamchallenge.easybuy.shop.event.ShopDeletedEvent;
import com.teamchallenge.easybuy.shop.event.ShopUpdatedEvent;
import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.dto.ShopDTO;
import com.teamchallenge.easybuy.shop.dto.ShopSearchParams;
import com.teamchallenge.easybuy.shop.dto.ShopSeoSettingsDTO;
import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import com.teamchallenge.easybuy.shop.exception.ShopNotFoundException;
import com.teamchallenge.easybuy.shop.mapper.ShopMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.ShopSearchBuilder;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import com.teamchallenge.easybuy.shop.service.shopcontactinfo.ShopContactInfoService;
import com.teamchallenge.easybuy.shop.service.shopseosettings.ShopSeoSettingsService;
import com.teamchallenge.easybuy.shop.service.shoptaxinfo.ShopTaxService;
import com.teamchallenge.easybuy.shop.service.validation.ShopValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * 🏪 Clean and maintainable Shop Service
 *
 * Responsibilities:
 * - CRUD operations for shops
 * - Basic business validation
 * - Event publishing for side effects
 *
 * What this service does NOT do:
 * - Endpoint-level auth wiring (handled by Spring Security annotations)
 * - Complex analytics (separate service)
 * - Email notifications (event listeners)
 * - Caching (repository/infrastructure layer)
 * - File processing (separate service)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final UserRepository userRepository;
    private final ShopValidationService validationService;
    private final ApplicationEventPublisher eventPublisher;
    private final ShopAccessGuard accessGuard;
    private final ShopContactInfoService shopContactInfoService;
    private final ShopTaxService shopTaxService;
    private final ShopSeoSettingsService shopSeoSettingsService;

    // === READ OPERATIONS ===

    @Transactional(readOnly = true)
    public Page<ShopDTO> getAllShops(Pageable pageable) {
        log.debug("Fetching shops with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return shopRepository.findAll(pageable).map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ShopDTO getShopById(@NotNull UUID id) {
        log.debug("Fetching shop by ID: {}", id);
        return shopMapper.toDto(findShopOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<ShopDTO> searchShops(@NotNull ShopSearchParams params, @NotNull Pageable pageable) {
        log.debug("Searching shops with params: {}", params);
        Specification<Shop> spec = ShopSearchBuilder.builder().withParams(params).build();
        return shopRepository.findAll(spec, pageable).map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ShopDTO> getShopsBySeller(@NotNull UUID sellerId, @NotNull Pageable pageable) {
        if (accessGuard.isCurrentUserSeller() && !accessGuard.isCurrentUserAdmin()) {
            UUID currentSellerId = accessGuard.getCurrentSellerOrThrow().getId();
            if (!currentSellerId.equals(sellerId)) {
                throw new IllegalArgumentException("You can read only your own shops");
            }
        }
        log.debug("Fetching shops for seller: {}", sellerId);
        return shopRepository.findBySellerId(sellerId, pageable).map(shopMapper::toDto);
    }

    // === WRITE OPERATIONS ===

    @Retryable(
            retryFor = {DataIntegrityViolationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public ShopDTO createShop(@Valid @NotNull ShopDTO shopDTO) {
        log.info("Creating shop: {}", shopDTO.getShopName());

        if (accessGuard.isCurrentUserSeller() && !accessGuard.isCurrentUserAdmin()) {
            shopDTO.setSellerId(accessGuard.getCurrentSellerOrThrow().getId());
            shopDTO.setModeratedByUserId(null);
            shopDTO.setShopStatus(Shop.ShopStatus.PENDING);
            shopDTO.setVerified(false);
            shopDTO.setFeatured(false);
        }

        validationService.validateForCreation(shopDTO);

        Shop shop = shopMapper.toEntity(shopDTO);
        setShopRelations(shop, shopDTO);
        setDefaultsForNewShop(shop);

        shop = shopRepository.save(shop);
        eventPublisher.publishEvent(new ShopCreatedEvent(shop));

        log.info("Created shop: {} with ID: {}", shop.getShopName(), shop.getShopId());
        return shopMapper.toDto(shop);
    }

    public ShopDTO updateShop(@NotNull UUID id, @Valid @NotNull ShopDTO shopDTO) {
        log.info("Updating shop: {}", id);
        Shop existingShop = findShopOrThrow(id);
        accessGuard.requireCanManageShop(existingShop);

        boolean sellerContext = accessGuard.isCurrentUserSeller() && !accessGuard.isCurrentUserAdmin();
        Shop.ShopStatus previousStatus = existingShop.getShopStatus();
        boolean previousVerified = existingShop.isVerified();
        boolean previousFeatured = existingShop.isFeatured();
        String previousRejectionReason = existingShop.getRejectionReason();
        String previousModeratorNotes = existingShop.getModeratorNotes();
        java.time.Instant previousLastModeratedAt = existingShop.getLastModeratedAt();
        var previousModerator = existingShop.getModeratedByUser();
        var previousSeller = existingShop.getSeller();

        validationService.validateForUpdate(existingShop, shopDTO);

        shopMapper.updateEntityFromDto(shopDTO, existingShop);
        setShopRelations(existingShop, shopDTO);

        if (sellerContext) {
            existingShop.setShopStatus(previousStatus);
            existingShop.setVerified(previousVerified);
            existingShop.setFeatured(previousFeatured);
            existingShop.setRejectionReason(previousRejectionReason);
            existingShop.setModeratorNotes(previousModeratorNotes);
            existingShop.setLastModeratedAt(previousLastModeratedAt);
            existingShop.setModeratedByUser(previousModerator);
            existingShop.setSeller(previousSeller);
        }

        Shop updatedShop = shopRepository.save(existingShop);
        eventPublisher.publishEvent(new ShopUpdatedEvent(updatedShop));

        log.info("Updated shop: {}", id);
        return shopMapper.toDto(updatedShop);
    }

    public ShopDTO patchShop(@NotNull UUID id, @NotNull @Valid ShopDTO updates) {
        // Optional partial update method
        return updateShop(id, updates);
    }

    public void deleteShop(@NotNull UUID id) {
        log.info("Deleting shop: {}", id);
        Shop shop = findShopOrThrow(id);
        accessGuard.requireCanManageShop(shop);

        if (!accessGuard.isCurrentUserAdmin()) {
            throw new IllegalStateException("Only admin can delete shops");
        }

        validationService.validateForDeletion(shop);

        shopRepository.delete(shop);
        eventPublisher.publishEvent(new ShopDeletedEvent(shop));

        log.info("Deleted shop: {}", id);
    }

    // === UTILITY OPERATIONS ===

    @Transactional(readOnly = true)
    public boolean existsByName(@NotNull String shopName) {
        return shopRepository.existsByShopName(shopName.trim());
    }

    @Transactional(readOnly = true)
    public boolean existsBySlug(@NotNull String slug) {
        return shopRepository.existsBySlug(slug.trim().toLowerCase());
    }

    public String generateSlug(@NotNull String shopName) {
        String baseSlug = normalizeSlug(shopName);

        if (!existsBySlug(baseSlug)) return baseSlug;

        int counter = 1;
        String candidateSlug;
        do {
            candidateSlug = baseSlug + "-" + counter++;
        } while (existsBySlug(candidateSlug));

        return candidateSlug;
    }

    // === PRIVATE HELPERS ===

    private Shop findShopOrThrow(UUID id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found: " + id));
    }

    private void setShopRelations(Shop shop, ShopDTO dto) {
        if (accessGuard.isCurrentUserSeller() && !accessGuard.isCurrentUserAdmin()) {
            shop.setSeller(accessGuard.getCurrentSellerOrThrow());
        } else if (dto.getSellerId() != null && accessGuard.isCurrentUserAdmin()) {
            shop.setSeller(userRepository.findById(dto.getSellerId())
                    .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + dto.getSellerId())));
        }

        if (dto.getModeratedByUserId() != null && accessGuard.isCurrentUserAdmin()) {
            shop.setModeratedByUser(userRepository.findById(dto.getModeratedByUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Moderator not found: " + dto.getModeratedByUserId())));
        }
    }

    /**
     * Updates shop and related sub-resources in one transaction for single-page frontend editing.
     */
    public ShopDTO updateShopProfile(@NotNull UUID id, @NotNull ShopDTO shopDTO) {
        ShopDTO updatedShop = updateShop(id, shopDTO);

        ShopContactInfoDTO contactInfo = shopDTO.getShopContactInfo();
        if (contactInfo != null) {
            upsertContactInfo(id, contactInfo);
        }

        ShopTaxInfoDTO taxInfo = shopDTO.getShopTaxInfo();
        if (taxInfo != null) {
            upsertTaxInfo(id, taxInfo);
        }

        ShopSeoSettingsDTO seoSettings = shopDTO.getSeoSettings();
        if (seoSettings != null) {
            upsertSeoSettings(id, seoSettings);
        }

        return getShopById(updatedShop.getShopId());
    }

    private void upsertContactInfo(UUID shopId, ShopContactInfoDTO dto) {
        try {
            shopContactInfoService.update(shopId, dto);
        } catch (IllegalArgumentException ex) {
            shopContactInfoService.create(shopId, dto);
        }
    }

    private void upsertTaxInfo(UUID shopId, ShopTaxInfoDTO dto) {
        try {
            shopTaxService.update(shopId, dto);
        } catch (IllegalArgumentException ex) {
            shopTaxService.create(shopId, dto);
        }
    }

    private void upsertSeoSettings(UUID shopId, ShopSeoSettingsDTO dto) {
        try {
            shopSeoSettingsService.update(shopId, dto);
        } catch (IllegalArgumentException ex) {
            shopSeoSettingsService.create(shopId, dto);
        }
    }

    private void setDefaultsForNewShop(Shop shop) {
        if (shop.getShopStatus() == null) shop.setShopStatus(Shop.ShopStatus.PENDING);
        if (shop.getShopType() == null) shop.setShopType(Shop.ShopType.RETAILER);
        if (!StringUtils.hasText(shop.getCurrency())) shop.setCurrency("UAH");
        if (!StringUtils.hasText(shop.getLanguage())) shop.setLanguage("uk");
        if (!StringUtils.hasText(shop.getTimezone())) shop.setTimezone("Europe/Kyiv");
        if (!StringUtils.hasText(shop.getSlug()) && StringUtils.hasText(shop.getShopName())) {
            shop.setSlug(generateSlug(shop.getShopName()));
        }
    }

    private String normalizeSlug(String input) {
        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}