package com.teamchallenge.easybuy.shop.service.audit;

import com.teamchallenge.easybuy.shop.entity.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Audit Service - user action auditing
 * *
 * * Features:
 * * - Logging store operations
 * * - Change tracking
 * * - Saving security context
 * * - Asynchronous processing
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    @Async("auditTaskExecutor")
    public CompletableFuture<Void> logShopCreation(Shop shop) {
        try {
            String currentUser = getCurrentUsername();

            log.info("[AUDIT] Shop created: {} by user: {} at: {}",
                    shop.getShopName(),
                    currentUser,
                    LocalDateTime.now());


            logAuditEvent(
                    "SHOP_CREATED",
                    shop.getShopId(),
                    currentUser,
                    String.format("Shop '%s' created", shop.getShopName())
            );

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to log shop creation audit for: {}", shop.getShopName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("auditTaskExecutor")
    public CompletableFuture<Void> logShopUpdate(Shop shop) {
        try {
            String currentUser = getCurrentUsername();

            log.info("[AUDIT] Shop updated: {} by user: {} at: {}",
                    shop.getShopName(),
                    currentUser,
                    LocalDateTime.now());

            logAuditEvent(
                    "SHOP_UPDATED",
                    shop.getShopId(),
                    currentUser,
                    String.format("Shop '%s' updated", shop.getShopName())
            );

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to log shop update audit for: {}", shop.getShopName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("auditTaskExecutor")
    public CompletableFuture<Void> logShopDeletion(Shop shop) {
        try {
            String currentUser = getCurrentUsername();

            log.warn("[AUDIT] Shop deleted: {} by user: {} at: {}",
                    shop.getShopName(),
                    currentUser,
                    LocalDateTime.now());

            logAuditEvent(
                    "SHOP_DELETED",
                    shop.getShopId(),
                    currentUser,
                    String.format("Shop '%s' deleted", shop.getShopName())
            );

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to log shop deletion audit for: {}", shop.getShopName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("auditTaskExecutor")
    public CompletableFuture<Void> logShopAccess(UUID shopId, String action) {
        try {
            String currentUser = getCurrentUsername();

            log.debug("[AUDIT] Shop {} accessed: {} by user: {} at: {}",
                    action.toLowerCase(),
                    shopId,
                    currentUser,
                    LocalDateTime.now());

            logAuditEvent(
                    "SHOP_" + action,
                    shopId,
                    currentUser,
                    String.format("Shop %s by %s", action.toLowerCase(), currentUser)
            );

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to log shop access audit for: {}", shopId, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async("auditTaskExecutor")
    public CompletableFuture<Void> logShopStatusChange(Shop shop, Shop.ShopStatus oldStatus, Shop.ShopStatus newStatus) {
        try {
            String currentUser = getCurrentUsername();

            log.info("[AUDIT] Shop status changed: {} ({} -> {}) by user: {} at: {}",
                    shop.getShopName(),
                    oldStatus,
                    newStatus,
                    currentUser,
                    LocalDateTime.now());

            logAuditEvent(
                    "SHOP_STATUS_CHANGED",
                    shop.getShopId(),
                    currentUser,
                    String.format("Status changed from %s to %s", oldStatus, newStatus)
            );

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to log shop status change audit for: {}", shop.getShopName(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    // === PRIVATE HELPERS ===

    private String getCurrentUsername() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null ? authentication.getName() : "SYSTEM";
        } catch (Exception e) {
            log.warn("Failed to get current username for audit", e);
            return "UNKNOWN";
        }
    }

    private void logAuditEvent(String eventType, UUID resourceId, String username, String description) {
        // todo:This can be saved to a separate audit tabl or sent to an external monitoring system

        log.info("[AUDIT_EVENT] Type: {} | Resource: {} | UserEntity: {} | Description: {} | Timestamp: {}",
                eventType,
                resourceId,
                username,
                description,
                LocalDateTime.now());
    }
}