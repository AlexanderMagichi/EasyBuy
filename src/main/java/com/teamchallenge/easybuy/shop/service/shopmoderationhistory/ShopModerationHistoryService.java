package com.teamchallenge.easybuy.shop.service.shopmoderationhistory;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopModerationHistory;
import com.teamchallenge.easybuy.user.entity.User;
import com.teamchallenge.easybuy.shop.dto.shopmoderationhistory.ShopModerationHistoryDTO;
import com.teamchallenge.easybuy.shop.dto.shopmoderationhistory.ShopModerationReversalDTO;
import com.teamchallenge.easybuy.shop.exception.ShopNotFoundException;
import com.teamchallenge.easybuy.shop.mapper.ShopModerationHistoryMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shopmoderationhistory.ShopModerationHistoryRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Provides business operations for ShopModerationHistoryService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopModerationHistoryService {

    private final ShopModerationHistoryRepository moderationHistoryRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final ShopModerationHistoryMapper mapper;
    private final ShopAccessGuard accessGuard;

    @Transactional(readOnly = true)
    public List<ShopModerationHistoryDTO> getByShopId(@NotNull UUID shopId) {
        accessGuard.requireCanManageShop(shopId);
        log.debug("Fetching moderation history for shop: {}", shopId);
        return moderationHistoryRepository.findByShop_ShopIdOrderByCreatedAtDesc(shopId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShopModerationHistoryDTO getById(@NotNull UUID shopId, @NotNull UUID moderationHistoryId) {
        accessGuard.requireCanManageShop(shopId);
        log.debug("Fetching moderation history record {} for shop: {}", moderationHistoryId, shopId);
        ShopModerationHistory historyRecord = findRecordOrThrow(shopId, moderationHistoryId);
        return mapper.toDto(historyRecord);
    }

    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            backoff = @Backoff(delay = 500)
    )
    public ShopModerationHistoryDTO create(@NotNull UUID shopId,
                                           @Valid @NotNull ShopModerationHistoryDTO dto) {
        accessGuard.requireAdmin();
        log.info("Creating moderation history record for shop: {}", shopId);

        Shop shop = findShopOrThrow(shopId);
        User moderator = findUserOrThrow(dto.getModeratorId(), "Moderator not found: ");

        ShopModerationHistory entity = mapper.toEntity(dto);
        entity.setShop(shop);
        entity.setModerator(moderator);

        if (entity.getSeverityLevel() == null) {
            entity.setSeverityLevel(entity.getDefaultSeverityLevel());
        }

        entity.calculateImpactScore();

        ShopModerationHistory saved = moderationHistoryRepository.save(entity);
        log.info("Created moderation history record {} for shop: {}", saved.getModerationHistoryId(), shopId);

        return mapper.toDto(saved);
    }

    public ShopModerationHistoryDTO reverse(@NotNull UUID shopId,
                                            @NotNull UUID moderationHistoryId,
                                            @Valid @NotNull ShopModerationReversalDTO dto) {
        accessGuard.requireAdmin();
        log.info("Reversing moderation history record {} for shop: {}", moderationHistoryId, shopId);

        ShopModerationHistory historyRecord = findRecordOrThrow(shopId, moderationHistoryId);

        if (!historyRecord.isReversible()) {
            throw new IllegalStateException("Moderation action is not reversible: " + moderationHistoryId);
        }

        if (historyRecord.getReversedAt() != null) {
            throw new IllegalStateException("Moderation action already reversed: " + moderationHistoryId);
        }

        User reversedBy = findUserOrThrow(dto.getReversedByUserId(), "User for reversal not found: ");
        historyRecord.setReversedByUser(reversedBy);
        historyRecord.setReversalReason(dto.getReversalReason());
        historyRecord.setReversedAt(Instant.now());

        ShopModerationHistory saved = moderationHistoryRepository.save(historyRecord);
        log.info("Reversed moderation history record {} for shop: {}", moderationHistoryId, shopId);

        return mapper.toDto(saved);
    }

    private Shop findShopOrThrow(UUID shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException("Shop not found: " + shopId));
    }

    private User findUserOrThrow(UUID userId, String messagePrefix) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(messagePrefix + userId));
    }

    private ShopModerationHistory findRecordOrThrow(UUID shopId, UUID moderationHistoryId) {
        return moderationHistoryRepository.findByModerationHistoryIdAndShop_ShopId(moderationHistoryId, shopId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Moderation history record not found: " + moderationHistoryId + " for shop: " + shopId
                ));
    }
}

