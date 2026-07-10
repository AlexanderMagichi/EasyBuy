package com.teamchallenge.easybuy.shop.controller.shopseosettings;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.shop.dto.ShopSeoSettingsDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.shop.service.shopseosettings.ShopSeoSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShopSeoSettingsControllerTest {

    @Mock
    private ShopSeoSettingsService service;

    @InjectMocks
    private ShopSeoSettingsController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseCreator()))
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/shops/{id}/seo-settings should return 201 for valid request")
    void create_valid_shouldReturn201() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopSeoSettingsDTO request = validRequest();
        ShopSeoSettingsDTO response = validRequest();
        response.setId(shopId);

        when(service.create(eq(shopId), any(ShopSeoSettingsDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/shops/{shopId}/seo-settings", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(shopId.toString()))
                .andExpect(jsonPath("$.metaTitle").value(request.getMetaTitle()));
    }

    @Test
    @DisplayName("POST /api/v1/shops/{id}/seo-settings should return 400 for invalid request")
    void create_invalid_shouldReturn400() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopSeoSettingsDTO request = new ShopSeoSettingsDTO();
        request.setMetaTitle("x".repeat(61));

        mockMvc.perform(post("/api/v1/shops/{shopId}/seo-settings", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/v1/shops/{id}/seo-settings should return 200")
    void patch_valid_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopSeoSettingsDTO request = new ShopSeoSettingsDTO();
        request.setMetaDescription("Updated description");

        ShopSeoSettingsDTO response = new ShopSeoSettingsDTO();
        response.setId(shopId);
        response.setMetaDescription("Updated description");

        when(service.patch(eq(shopId), any(ShopSeoSettingsDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/shops/{shopId}/seo-settings", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shopId.toString()));
    }

    @Test
    @DisplayName("DELETE /api/v1/shops/{id}/seo-settings should return 204")
    void delete_shouldReturn204() throws Exception {
        UUID shopId = UUID.randomUUID();
        doNothing().when(service).delete(shopId);

        mockMvc.perform(delete("/api/v1/shops/{shopId}/seo-settings", shopId))
                .andExpect(status().isNoContent());
    }

    private ShopSeoSettingsDTO validRequest() {
        ShopSeoSettingsDTO dto = new ShopSeoSettingsDTO();
        dto.setMetaTitle("Best Shop in Kyiv");
        dto.setMetaDescription("High-quality goods and fast delivery");
        dto.setMetaKeywords("shop, delivery, kyiv");
        dto.setCanonicalUrl("https://example.com/shop");
        return dto;
    }
}

