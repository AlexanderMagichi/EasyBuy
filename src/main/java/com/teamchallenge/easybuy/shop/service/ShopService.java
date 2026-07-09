package com.teamchallenge.easybuy.shop.service;

import com.teamchallenge.easybuy.shop.dto.ShopDTO;
import com.teamchallenge.easybuy.shop.dto.ShopSearchParams;
import com.teamchallenge.easybuy.shop.dto.ShopSeoSettingsDTO;
import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.event.ShopCreatedEvent;
import com.teamchallenge.easybuy.shop.event.ShopDeletedEvent;
import com.teamchallenge.easybuy.shop.event.ShopUpdatedEvent;
import com.teamchallenge.easybuy.shop.exception.ShopNotFoundException;
import com.teamchallenge.easybuy.shop.mapper.ShopMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.ShopSearchBuilder;
import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import com.teamchallenge.easybuy.shop.service.shopcontactinfo.ShopContactInfoService;
import com.teamchallenge.easybuy.shop.service.shopseosettings.ShopSeoSettingsService;
import com.teamchallenge.easybuy.shop.service.shoptaxinfo.ShopTaxService;
import com.teamchallenge.easybuy.shop.service.validation.ShopValidationService;
import com.teamchallenge.easybuy.user.entity.MembershipStatus;
import com.teamchallenge.easybuy.user.entity.StoreMembership;
import com.teamchallenge.easybuy.user.entity.StoreMembershipRole;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.repository.StoreMembershipRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopMapper shopMapper;
    private final UserRepository userRepository;
    private final StoreMembershipRepository storeMembershipRepository;
    private final ShopValidationService validationService;
    private final ApplicationEventPublisher eventPublisher;
    private final ShopAccessGuard accessGuard;
    private final ShopContactInfoService shopContactInfoService;
    private final ShopTaxService shopTaxService;
    private final ShopSeoSettingsService shopSeoSettingsService;

    @Transactional(readOnly = true)
    public Page<ShopDTO> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable).map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ShopDTO getShopById(@NotNull UUID id) {
        return shopMapper.toDto(findShopOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<ShopDTO> searchShops(@NotNull ShopSearchParams params, @NotNull Pageable pageable) {
        Specification<Shop> spec = ShopSearchBuilder.builder().withParams(params).build();
        return shopRepository.findAll(spec, pageable).map(shopMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ShopDTO> getShopsByUserId(@NotNull UUID userId, @NotNull Pageable pageable) {
        if (!accessGuard.isCurrentUserAdmin()) {
            if (!accessGuard.getCurrentUserIdOrThrow().equals(userId)) {
                throw new IllegalArgumentException("Access denied");
            }
        }
        return shopRepository.findShopsByUserId(userId, pageable).map(shopMapper::toDto);
    }

    @Retryable(retryFor = {DataIntegrityViolationException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public ShopDTO createShop(@Valid @NotNull ShopDTO shopDTO) {
        boolean isAdmin = accessGuard.isCurrentUserAdmin();
        UUID currentUserId = isAdmin ? null : accessGuard.getCurrentUserIdOrThrow();

        if (!isAdmin) {
            shopDTO.setModeratedByUserId(null);
            shopDTO.setShopStatus(Shop.ShopStatus.PENDING);
            shopDTO.setVerified(false);
            shopDTO.setFeatured(false);
        }

        validationService.validateForCreation(shopDTO);
        Shop shop = shopMapper.toEntity(shopDTO);
        setDefaultsForNewShop(shop);
        shop = shopRepository.save(shop);

        if (!isAdmin && currentUserId != null) {
            UserEntity creator = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new IllegalStateException("UserEntity not found"));
            storeMembershipRepository.save(StoreMembership.builder()
                    .user(creator)
                    .storeId(shop.getShopId())
                    .role(StoreMembershipRole.OWNER)
                    .status(MembershipStatus.ACTIVE)
                    .build());
        }

        eventPublisher.publishEvent(new ShopCreatedEvent(shop));
        return shopMapper.toDto(shop);
    }

    public ShopDTO updateShop(@NotNull UUID id, @Valid @NotNull ShopDTO shopDTO) {
        Shop existingShop = findShopOrThrow(id);
        accessGuard.requireCanManageShop(existingShop);

        boolean isUserContext = !accessGuard.isCurrentUserAdmin();
        Shop.ShopStatus prevStatus = existingShop.getShopStatus();
        boolean prevVerified = existingShop.isVerified();
        boolean prevFeatured = existingShop.isFeatured();

        validationService.validateForUpdate(existingShop, shopDTO);
        shopMapper.updateEntityFromDto(shopDTO, existingShop);

        if (isUserContext) {
            existingShop.setShopStatus(prevStatus);
            existingShop.setVerified(prevVerified);
            existingShop.setFeatured(prevFeatured);
        }

        Shop updated = shopRepository.save(existingShop);
        eventPublisher.publishEvent(new ShopUpdatedEvent(updated));
        return shopMapper.toDto(updated);
    }

    public ShopDTO patchShop(@NotNull UUID id, @NotNull @Valid ShopDTO updates) {
        return updateShop(id, updates);
    }

    public void deleteShop(@NotNull UUID id) {
        Shop shop = findShopOrThrow(id);
        accessGuard.requireCanManageShop(shop);
        if (!accessGuard.isCurrentUserAdmin()) {
            throw new IllegalStateException("Only admin can delete shops");
        }
        validationService.validateForDeletion(shop);
        shopRepository.delete(shop);
        eventPublisher.publishEvent(new ShopDeletedEvent(shop));
    }

    public boolean existsByName(@NotNull String shopName) {
        return shopRepository.existsByShopName(shopName.trim());
    }

    public boolean existsBySlug(@NotNull String slug) {
        return shopRepository.existsBySlug(slug.trim().toLowerCase());
    }

    public String generateSlug(@NotNull String shopName) {
        String base = normalizeSlug(shopName);
        if (!existsBySlug(base)) return base;
        int counter = 1;
        String candidate;
        do { candidate = base + "-" + counter++; } while (existsBySlug(candidate));
        return candidate;
    }

    private Shop findShopOrThrow(UUID id) {
        return shopRepository.findById(id).orElseThrow(() -> new ShopNotFoundException("Shop not found: " + id));
    }

    private void setShopRelations(Shop shop, ShopDTO dto) {
        if (dto.getModeratedByUserId() != null && accessGuard.isCurrentUserAdmin()) {
            shop.setModeratedByUser(userRepository.findById(dto.getModeratedByUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Moderator not found")));
        }
    }

    public ShopDTO updateShopProfile(@NotNull UUID id, @NotNull ShopDTO shopDTO) {
        ShopDTO updated = updateShop(id, shopDTO);
        if (shopDTO.getShopContactInfo() != null) upsertContactInfo(id, shopDTO.getShopContactInfo());
        if (shopDTO.getShopTaxInfo() != null) upsertTaxInfo(id, shopDTO.getShopTaxInfo());
        if (shopDTO.getSeoSettings() != null) upsertSeoSettings(id, shopDTO.getSeoSettings());
        return getShopById(updated.getShopId());
    }

    private void upsertContactInfo(UUID id, ShopContactInfoDTO dto) {
        try { shopContactInfoService.update(id, dto); } catch (Exception e) { shopContactInfoService.create(id, dto); }
    }

    private void upsertTaxInfo(UUID id, ShopTaxInfoDTO dto) {
        try { shopTaxService.update(id, dto); } catch (Exception e) { shopTaxService.create(id, dto); }
    }

    private void upsertSeoSettings(UUID id, ShopSeoSettingsDTO dto) {
        try { shopSeoSettingsService.update(id, dto); } catch (Exception e) { shopSeoSettingsService.create(id, dto); }
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
        return input.trim().toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("-+",
                "-").replaceAll("^-|-$", "");
    }
}