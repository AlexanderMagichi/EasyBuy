package com.teamchallenge.easybuy.shop.controller.shopbillinginfo;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.shop.dto.shopbillinginfo.ShopBillingInfoDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.shop.exception.ShopBillingIntegrationException;
import com.teamchallenge.easybuy.shop.service.shopbillingservice.ShopBillingService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShopBillingControllerTest {

    @Mock
    private ShopBillingService billingService;

    @InjectMocks
    private ShopBillingController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseCreator()))
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/shops/{id}/billing should return 200")
    void getBillingInfo_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();
        ShopBillingInfoDTO response = ShopBillingInfoDTO.builder()
                .id(shopId)
                .billingEmail("finance@example.com")
                .defaultCurrency("USD")
                .build();

        when(billingService.getBillingInfo(shopId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/shops/{shopId}/billing", shopId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shopId.toString()))
                .andExpect(jsonPath("$.billingEmail").value("finance@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/shops/{id}/billing/onboarding should return 200")
    void setupOnboarding_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();
        ShopBillingInfoDTO response = ShopBillingInfoDTO.builder()
                .id(shopId)
                .stripeAccountId("acct_test_123")
                .onboardingUrl("https://connect.stripe.com/test")
                .build();

        when(billingService.setupStripeOnboarding(shopId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/shops/{shopId}/billing/onboarding", shopId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shopId.toString()))
                .andExpect(jsonPath("$.stripeAccountId").value("acct_test_123"));
    }

    @Test
    @DisplayName("GET /api/v1/shops/{id}/billing should return 404 when info is missing")
    void getBillingInfo_notFound_shouldReturn404() throws Exception {
        UUID shopId = UUID.randomUUID();
        when(billingService.getBillingInfo(shopId))
                .thenThrow(new EntityNotFoundException("Billing info not found"));

        mockMvc.perform(get("/api/v1/shops/{shopId}/billing", shopId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/shops/{id}/billing/onboarding should return 502 when stripe fails")
    void setupOnboarding_stripeFailure_shouldReturn502() throws Exception {
        UUID shopId = UUID.randomUUID();
        when(billingService.setupStripeOnboarding(shopId))
                .thenThrow(new ShopBillingIntegrationException("Stripe service communication error", new RuntimeException("upstream")));

        mockMvc.perform(post("/api/v1/shops/{shopId}/billing/onboarding", shopId))
                .andExpect(status().isBadGateway());
    }
}

