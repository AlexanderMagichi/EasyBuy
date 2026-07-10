package com.teamchallenge.easybuy.shop.controller.shopcontactinfo;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.shop.service.shopcontactinfo.ShopContactInfoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ShopContactInfoControllerTest {

    @Mock
    private ShopContactInfoService service;

    @InjectMocks
    private ShopContactInfoController controller;

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
    @DisplayName("POST /api/v1/shops/{id}/contact-info should return 201 for valid request")
    void create_valid_shouldReturn201() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopContactInfoDTO request = validRequest();
        ShopContactInfoDTO response = validRequest();
        response.setContactInfoId(UUID.randomUUID());

        when(service.create(eq(shopId), any(ShopContactInfoDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/shops/{shopId}/contact-info", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contactInfoId").value(response.getContactInfoId().toString()))
                .andExpect(jsonPath("$.contactEmail").value(request.getContactEmail()));
    }

    @Test
    @DisplayName("POST /api/v1/shops/{id}/contact-info should return 400 for invalid request")
    void create_invalid_shouldReturn400() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopContactInfoDTO request = new ShopContactInfoDTO();
        request.setContactEmail("invalid-email");
        request.setContactPhone(" ");

        mockMvc.perform(post("/api/v1/shops/{shopId}/contact-info", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/shops/{id}/contact-info should return 200")
    void update_valid_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopContactInfoDTO request = validRequest();
        ShopContactInfoDTO response = validRequest();
        response.setContactInfoId(UUID.randomUUID());

        when(service.update(eq(shopId), any(ShopContactInfoDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/shops/{shopId}/contact-info", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactInfoId").value(response.getContactInfoId().toString()));
    }

    @Test
    @DisplayName("DELETE /api/v1/shops/{id}/contact-info should return 204")
    void deactivate_shouldReturn204() throws Exception {
        UUID shopId = UUID.randomUUID();
        doNothing().when(service).deactivate(shopId);

        mockMvc.perform(delete("/api/v1/shops/{shopId}/contact-info", shopId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/v1/shops/{id}/contact-info/verify should return 200")
    void verify_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();
        doNothing().when(service).verify(shopId);

        mockMvc.perform(post("/api/v1/shops/{shopId}/contact-info/verify", shopId))
                .andExpect(status().isOk());
    }

    private ShopContactInfoDTO validRequest() {
        ShopContactInfoDTO dto = new ShopContactInfoDTO();
        dto.setContactEmail("contact@example.com");
        dto.setContactPhone("+380931112233");
        dto.setBusinessAddress("Kyiv, Main st. 1");
        dto.setCity("Kyiv");
        dto.setCountry("UA");
        return dto;
    }
}

