package com.teamchallenge.easybuy.shop.service.shopbillingservice;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.stripe.exception.StripeException;
import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopBillingInfo;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.shop.dto.shopbillinginfo.ShopBillingInfoDTO;
import com.teamchallenge.easybuy.shop.exception.ShopBillingIntegrationException;
import com.teamchallenge.easybuy.shop.mapper.ShopBillingMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shopbillinginfo.ShopBillingRepository;
import com.teamchallenge.easybuy.payment.service.StripeService;
import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopBillingServiceTest {

    @Mock
    private ShopBillingRepository billingRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ShopBillingMapper billingMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private ShopAccessGuard accessGuard;

    @InjectMocks
    private ShopBillingService service;

    @Test
    @DisplayName("getBillingInfo should throw when billing info does not exist")
    void getBillingInfo_notFound_shouldThrow() {
        UUID shopId = UUID.randomUUID();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(billingRepository.findById(shopId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getBillingInfo(shopId));
    }

    @Test
    @DisplayName("setupStripeOnboarding should create billing and stripe account for shop without billing")
    void setupOnboarding_withoutBilling_shouldCreateAndReturnDto() throws Exception {
        UUID shopId = UUID.randomUUID();

        UserEntity seller = new UserEntity();
        seller.setEmail("seller@example.com");

        Shop shop = new Shop();
        shop.setShopId(shopId);
        shop.setSeller(seller);

        ShopBillingInfo createdBilling = ShopBillingInfo.builder()
                .shop(shop)
                .billingEmail("seller@example.com")
                .build();

        ShopBillingInfoDTO mapped = ShopBillingInfoDTO.builder().id(shopId).build();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(billingRepository.save(any(ShopBillingInfo.class))).thenReturn(createdBilling);
        when(stripeService.createStripeAccount("seller@example.com")).thenReturn("acct_test_123");
        when(stripeService.createOnboardingLink("acct_test_123")).thenReturn("https://connect.stripe.com/test");
        when(billingMapper.toDto(any(ShopBillingInfo.class))).thenReturn(mapped);

        ShopBillingInfoDTO result = service.setupStripeOnboarding(shopId);

        assertEquals("https://connect.stripe.com/test", result.getOnboardingUrl());
        verify(stripeService).createStripeAccount("seller@example.com");
    }

    @Test
    @DisplayName("setupStripeOnboarding should reuse existing stripe account")
    void setupOnboarding_existingStripe_shouldNotCreateAccountAgain() throws Exception {
        UUID shopId = UUID.randomUUID();

        UserEntity seller = new UserEntity();
        seller.setEmail("seller@example.com");

        ShopBillingInfo billing = ShopBillingInfo.builder()
                .stripeAccountId("acct_existing")
                .billingEmail("seller@example.com")
                .build();

        Shop shop = new Shop();
        shop.setShopId(shopId);
        shop.setSeller(seller);
        shop.setShopBillingInfo(billing);

        ShopBillingInfoDTO mapped = ShopBillingInfoDTO.builder().id(shopId).build();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(stripeService.createOnboardingLink("acct_existing")).thenReturn("https://connect.stripe.com/existing");
        when(billingRepository.save(any(ShopBillingInfo.class))).thenReturn(billing);
        when(billingMapper.toDto(any(ShopBillingInfo.class))).thenReturn(mapped);

        ShopBillingInfoDTO result = service.setupStripeOnboarding(shopId);

        assertEquals("https://connect.stripe.com/existing", result.getOnboardingUrl());
        verify(stripeService, never()).createStripeAccount(any());
    }

    @Test
    @DisplayName("setupStripeOnboarding should throw when shop is not found")
    void setupOnboarding_shopNotFound_shouldThrow() {
        UUID shopId = UUID.randomUUID();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.setupStripeOnboarding(shopId));
    }

    @Test
    @DisplayName("setupStripeOnboarding should wrap stripe account creation failure")
    void setupOnboarding_createAccountStripeFailure_shouldThrowIntegrationException() throws Exception {
        UUID shopId = UUID.randomUUID();

        UserEntity seller = new UserEntity();
        seller.setEmail("seller@example.com");

        Shop shop = new Shop();
        shop.setShopId(shopId);
        shop.setSeller(seller);

        ShopBillingInfo createdBilling = ShopBillingInfo.builder()
                .shop(shop)
                .billingEmail("seller@example.com")
                .build();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(billingRepository.save(any(ShopBillingInfo.class))).thenReturn(createdBilling);
        when(stripeService.createStripeAccount("seller@example.com"))
                .thenThrow(new StripeException("boom", null, null, 500) {});

        assertThrows(ShopBillingIntegrationException.class, () -> service.setupStripeOnboarding(shopId));
    }

    @Test
    @DisplayName("setupStripeOnboarding should wrap onboarding-link stripe failure")
    void setupOnboarding_createLinkStripeFailure_shouldThrowIntegrationException() throws Exception {
        UUID shopId = UUID.randomUUID();

        UserEntity seller = new UserEntity();
        seller.setEmail("seller@example.com");

        ShopBillingInfo billing = ShopBillingInfo.builder()
                .stripeAccountId("acct_existing")
                .billingEmail("seller@example.com")
                .build();

        Shop shop = new Shop();
        shop.setShopId(shopId);
        shop.setSeller(seller);
        shop.setShopBillingInfo(billing);

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(stripeService.createOnboardingLink("acct_existing"))
                .thenThrow(new StripeException("boom", null, null, 500) {});

        assertThrows(ShopBillingIntegrationException.class, () -> service.setupStripeOnboarding(shopId));
    }
}

