package com.teamchallenge.easybuy.shop.service.notification;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private Configuration freemarkerConfig;
    @Mock
    private Template template;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(mailSender, freemarkerConfig);
        ReflectionTestUtils.setField(notificationService, "fromEmail", "test@easybuy.local");
        ReflectionTestUtils.setField(notificationService, "frontendUrl", "http://localhost:3000");
        ReflectionTestUtils.setField(notificationService, "adminEmails", new String[]{"admin1@easybuy.local"});
    }

    @Test
    @DisplayName("sendShopCreatedEmail should return completed future for invalid shop")
    void sendShopCreatedEmail_invalidShop_returnsCompletedFuture() {
        CompletableFuture<Void> future = notificationService.sendShopCreatedEmail(null);

        assertNull(future.join());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendShopCreatedEmail should return exceptional future when template generation fails")
    void sendShopCreatedEmail_templateFailure_returnsExceptionalFuture() throws Exception {
        Shop shop = validShop();

        when(freemarkerConfig.getTemplate("email/shop_created.html")).thenThrow(new IOException("template not found"));

        CompletableFuture<Void> future = notificationService.sendShopCreatedEmail(shop);

        assertThrows(CompletionException.class, future::join);
    }

    @Test
    @DisplayName("sendShopUpdatedEmail should return exceptional future when template processing fails")
    void sendShopUpdatedEmail_templateProcessFailure_returnsExceptionalFuture() throws Exception {
        Shop shop = validShop();

        when(freemarkerConfig.getTemplate("email/shop_updated.html")).thenReturn(template);
        doAnswer(invocation -> {
            throw new IOException("template process failed");
        }).when(template).process(anyMap(), any(Writer.class));

        CompletableFuture<Void> future = notificationService.sendShopUpdatedEmail(shop);

        assertThrows(CompletionException.class, future::join);
    }

    @Test
    @DisplayName("sendNewShopNotificationToAdmins should return completed future when no admins configured")
    void sendNewShopNotificationToAdmins_noAdmins_returnsCompletedFuture() {
        ReflectionTestUtils.setField(notificationService, "adminEmails", new String[0]);
        Shop shop = validShop();

        CompletableFuture<Void> future = notificationService.sendNewShopNotificationToAdmins(shop);

        assertNull(future.join());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendNewShopNotificationToAdmins should return exceptional future when template fails")
    void sendNewShopNotificationToAdmins_templateFailure_returnsExceptionalFuture() throws Exception {
        Shop shop = validShop();

        when(freemarkerConfig.getTemplate("email/new_shop_admin_notification.html"))
                .thenThrow(new IOException("admin template failed"));

        CompletableFuture<Void> future = notificationService.sendNewShopNotificationToAdmins(shop);

        assertThrows(CompletionException.class, future::join);
    }

    @Test
    @DisplayName("sendShopDeletedEmail should stay resilient and not throw synchronously")
    void sendShopDeletedEmail_shouldNotThrowSynchronously() throws Exception {
        Shop shop = validShop();

        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(freemarkerConfig.getTemplate("email/shop_deleted.html")).thenReturn(template);
        doAnswer(invocation -> {
            Writer writer = invocation.getArgument(1);
            writer.write("<html>deleted</html>");
            return null;
        }).when(template).process(anyMap(), any(Writer.class));
        doNothing().when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> notificationService.sendShopDeletedEmail(shop).join());
    }

    private Shop validShop() {
        UserEntity seller = new UserEntity();
        seller.setEmail("seller@easybuy.local");

        Shop shop = new Shop();
        shop.setShopName("Test Shop");
        shop.setShopDescription("Test description");
        shop.setSeller(seller);
        shop.setShopStatus(Shop.ShopStatus.ACTIVE);
        return shop;
    }
}

