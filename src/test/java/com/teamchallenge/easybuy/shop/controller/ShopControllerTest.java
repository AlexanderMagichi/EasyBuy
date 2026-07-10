package com.teamchallenge.easybuy.shop.controller;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.dto.ShopDTO;
import com.teamchallenge.easybuy.shop.dto.request.ShopCreateRequestDTO;
import com.teamchallenge.easybuy.shop.dto.request.ShopPatchRequestDTO;
import com.teamchallenge.easybuy.shop.dto.request.ShopUpdateRequestDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.shop.service.ShopService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    @Mock
    private ShopService shopService;

    @InjectMocks
    private ShopController shopController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(shopController)
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseCreator()))
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/shops should return 201 for valid request")
    void createShop_valid_shouldReturn201() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopCreateRequestDTO request = new ShopCreateRequestDTO();
        request.setShopName("My Shop");
        request.setShopDescription("Shop description");
        request.setCurrency("UAH");
        request.setLanguage("uk");
        request.setTimezone("Europe/Kyiv");

        ShopDTO response = new ShopDTO();
        response.setShopId(shopId);
        response.setShopName("My Shop");

        when(shopService.createShop(any(ShopDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shopId").value(shopId.toString()))
                .andExpect(jsonPath("$.shopName").value("My Shop"));
    }

    @Test
    @DisplayName("POST /api/v1/shops should return 400 for invalid request")
    void createShop_invalid_shouldReturn400() throws Exception {
        ShopCreateRequestDTO request = new ShopCreateRequestDTO();
        request.setShopName("  ");
        request.setShopDescription("  ");

        mockMvc.perform(post("/api/v1/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.shopName").exists())
                .andExpect(jsonPath("$.errors.shopDescription").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/shops/{id} should return 200 for valid request")
    void updateShop_valid_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopUpdateRequestDTO request = new ShopUpdateRequestDTO();
        request.setShopName("Updated Shop");
        request.setShopDescription("Updated description");
        request.setShopStatus(Shop.ShopStatus.ACTIVE);
        request.setCurrency("UAH");
        request.setLanguage("uk");
        request.setTimezone("Europe/Kyiv");

        ShopDTO response = new ShopDTO();
        response.setShopId(shopId);
        response.setShopName("Updated Shop");

        when(shopService.updateShop(eq(shopId), any(ShopDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/shops/{id}", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopId").value(shopId.toString()))
                .andExpect(jsonPath("$.shopName").value("Updated Shop"));
    }

    @Test
    @DisplayName("PUT /api/v1/shops/{id} should return 400 for invalid request")
    void updateShop_invalid_shouldReturn400() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopUpdateRequestDTO request = new ShopUpdateRequestDTO();
        request.setShopName("Updated Shop");
        request.setShopDescription("Updated description");
        request.setCurrency("");
        request.setLanguage("uk");
        request.setTimezone("Europe/Kyiv");

        mockMvc.perform(put("/api/v1/shops/{id}", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/v1/shops/{id} should return 200 for partial request")
    void patchShop_valid_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopPatchRequestDTO request = new ShopPatchRequestDTO();
        request.setShopDescription("Patched description");

        ShopDTO response = new ShopDTO();
        response.setShopId(shopId);
        response.setShopDescription("Patched description");

        when(shopService.patchShop(eq(shopId), any(ShopDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/shops/{id}", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopId").value(shopId.toString()));
    }

    @Test
    @DisplayName("PUT /api/v1/shops/{id}/profile should return 200")
    void updateProfile_valid_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopDTO request = new ShopDTO();
        request.setShopName("Profile Shop");
        request.setShopDescription("Profile description");

        ShopDTO response = new ShopDTO();
        response.setShopId(shopId);
        response.setShopName("Profile Shop");

        when(shopService.updateShopProfile(eq(shopId), any(ShopDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/shops/{id}/profile", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopId").value(shopId.toString()));
    }

    @Test
    @DisplayName("DELETE /api/v1/shops/{id} should return 204")
    void deleteShop_shouldReturn204() throws Exception {
        UUID shopId = UUID.randomUUID();
        doNothing().when(shopService).deleteShop(shopId);

        mockMvc.perform(delete("/api/v1/shops/{id}", shopId))
                .andExpect(status().isNoContent());
    }
}

