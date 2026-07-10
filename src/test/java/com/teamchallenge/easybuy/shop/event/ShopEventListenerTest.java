package com.teamchallenge.easybuy.shop.event;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.service.analytics.AnalyticsService;
import com.teamchallenge.easybuy.shop.service.audit.AuditService;
import com.teamchallenge.easybuy.shop.service.notification.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShopEventListenerTest {

    @Mock
    private NotificationService notificationService;
    @Mock
    private AnalyticsService analyticsService;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private ShopEventListener listener;

    @Test
    @DisplayName("handleShopCreated should not throw when notification future fails")
    void handleShopCreated_notificationFutureFailure_shouldNotThrow() {
        Shop shop = testShop("Created Shop");
        ShopCreatedEvent event = new ShopCreatedEvent(shop);

        when(notificationService.sendShopCreatedEmail(shop))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("mail failed")));
        when(analyticsService.recordShopCreation(shop))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(auditService.logShopCreation(shop))
                .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> listener.handleShopCreated(event));
        verify(analyticsService).recordShopCreation(shop);
        verify(auditService).logShopCreation(shop);
    }

    @Test
    @DisplayName("handleShopUpdated should not throw when analytics future fails")
    void handleShopUpdated_analyticsFutureFailure_shouldNotThrow() {
        Shop shop = testShop("Updated Shop");
        ShopUpdatedEvent event = new ShopUpdatedEvent(shop);

        when(notificationService.sendShopUpdatedEmail(shop))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(analyticsService.recordShopUpdate(shop))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("analytics failed")));
        when(auditService.logShopUpdate(shop))
                .thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> listener.handleShopUpdated(event));
        verify(notificationService).sendShopUpdatedEmail(shop);
        verify(auditService).logShopUpdate(shop);
    }

    @Test
    @DisplayName("handleShopDeleted should not throw when audit future fails")
    void handleShopDeleted_auditFutureFailure_shouldNotThrow() {
        Shop shop = testShop("Deleted Shop");
        ShopDeletedEvent event = new ShopDeletedEvent(shop);

        when(notificationService.sendShopDeletedEmail(shop))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(analyticsService.recordShopDeletion(shop))
                .thenReturn(CompletableFuture.completedFuture(null));
        when(auditService.logShopDeletion(shop))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("audit failed")));

        assertDoesNotThrow(() -> listener.handleShopDeleted(event));
        verify(notificationService).sendShopDeletedEmail(shop);
        verify(analyticsService).recordShopDeletion(shop);
    }

    private Shop testShop(String name) {
        Shop shop = new Shop();
        shop.setShopName(name);
        return shop;
    }
}

