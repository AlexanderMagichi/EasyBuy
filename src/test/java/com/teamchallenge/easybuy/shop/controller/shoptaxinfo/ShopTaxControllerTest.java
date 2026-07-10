package com.teamchallenge.easybuy.shop.controller.shoptaxinfo;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.shop.service.shoptaxinfo.ShopTaxService;
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
class ShopTaxControllerTest {

    @Mock
    private ShopTaxService service;

    @InjectMocks
    private ShopTaxController controller;

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
    @DisplayName("POST /api/v1/shops/{id}/tax-info should return 201 for valid request")
    void create_valid_shouldReturn201() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopTaxInfoDTO request = validRequest();
        ShopTaxInfoDTO response = validRequest();
        response.setId(shopId);

        when(service.create(eq(shopId), any(ShopTaxInfoDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/shops/{shopId}/tax-info", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(shopId.toString()))
                .andExpect(jsonPath("$.taxId").value(request.getTaxId()));
    }

    @Test
    @DisplayName("POST /api/v1/shops/{id}/tax-info should return 400 for invalid request")
    void create_invalid_shouldReturn400() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopTaxInfoDTO request = new ShopTaxInfoDTO();
        request.setTaxId(" ");
        request.setTaxpayerType(null);
        request.setLegalName(" ");

        mockMvc.perform(post("/api/v1/shops/{shopId}/tax-info", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/shops/{id}/tax-info should return 200")
    void update_valid_shouldReturn200() throws Exception {
        UUID shopId = UUID.randomUUID();

        ShopTaxInfoDTO request = validRequest();
        ShopTaxInfoDTO response = validRequest();
        response.setId(shopId);

        when(service.update(eq(shopId), any(ShopTaxInfoDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/shops/{shopId}/tax-info", shopId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shopId.toString()));
    }

    @Test
    @DisplayName("DELETE /api/v1/shops/{id}/tax-info should return 204")
    void delete_shouldReturn204() throws Exception {
        UUID shopId = UUID.randomUUID();
        doNothing().when(service).delete(shopId);

        mockMvc.perform(delete("/api/v1/shops/{shopId}/tax-info", shopId))
                .andExpect(status().isNoContent());
    }

    private ShopTaxInfoDTO validRequest() {
        ShopTaxInfoDTO dto = new ShopTaxInfoDTO();
        dto.setTaxId("1234567890");
        dto.setTaxpayerType("BUSINESS");
        dto.setLegalName("EasyBuy LLC");
        dto.setTaxCountryCode("UA");
        dto.setRegisteredAddress("Kyiv, Main st. 1");
        return dto;
    }
}

