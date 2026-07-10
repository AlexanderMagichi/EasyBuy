package com.teamchallenge.easybuy.product.controller.category;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.product.dto.category.GoodsAttributeValueDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.product.exception.GoodsAttributeValueException;
import com.teamchallenge.easybuy.product.service.category.GoodsAttributeValueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GoodsAttributeValueControllerTest {

    @Mock
    private GoodsAttributeValueService goodsAttributeValueService;

    @InjectMocks
    private GoodsAttributeValueController goodsAttributeValueController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private final UUID id = UUID.randomUUID();
    private final UUID goodsId = UUID.randomUUID();
    private final UUID attributeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(goodsAttributeValueController)
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseCreator()))
                .build();
    }

    @Test
    void getAllAttributeValues_shouldReturnList() throws Exception {
        GoodsAttributeValueDTO dto = new GoodsAttributeValueDTO();
        dto.setId(id);
        dto.setGoodsId(goodsId);
        dto.setAttributeId(attributeId);
        dto.setValue("Red");

        when(goodsAttributeValueService.getAllAttributeValues()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/attribute-values"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].value").value("Red"));
    }

    @Test
    void getAttributeValueById_shouldReturnAttributeValue() throws Exception {
        GoodsAttributeValueDTO dto = new GoodsAttributeValueDTO();
        dto.setId(id);
        dto.setValue("Red");

        when(goodsAttributeValueService.getAttributeValueById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/attribute-values/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.value").value("Red"));
    }

    @Test
    void getAttributeValueById_shouldReturn404WhenNotFound() throws Exception {
        when(goodsAttributeValueService.getAttributeValueById(id))
                .thenThrow(new GoodsAttributeValueException("Attribute value with ID " + id + " not found"));

        mockMvc.perform(get("/api/attribute-values/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Attribute value with ID " + id + " not found"));
    }

    @Test
    void createAttributeValue_shouldReturnCreatedAttributeValue() throws Exception {
        GoodsAttributeValueDTO dto = new GoodsAttributeValueDTO();
        dto.setGoodsId(goodsId);
        dto.setAttributeId(attributeId);
        dto.setValue("Red");

        when(goodsAttributeValueService.createAttributeValue(any(GoodsAttributeValueDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/attribute-values")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value("Red"));
    }

    @Test
    void updateAttributeValue_shouldReturnUpdatedAttributeValue() throws Exception {
        GoodsAttributeValueDTO dto = new GoodsAttributeValueDTO();
        dto.setId(id);
        dto.setValue("Blue");

        when(goodsAttributeValueService.updateAttributeValue(eq(id), any(GoodsAttributeValueDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/attribute-values/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("Blue"));
    }

    @Test
    void deleteAttributeValue_shouldReturn200() throws Exception {
        doNothing().when(goodsAttributeValueService).deleteAttributeValue(id);

        mockMvc.perform(delete("/api/attribute-values/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void searchAttributeValues_shouldReturnFilteredList() throws Exception {
        GoodsAttributeValueDTO dto = new GoodsAttributeValueDTO();
        dto.setId(id);
        dto.setValue("Red");

        when(goodsAttributeValueService.searchAttributeValues(goodsId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/attribute-values/search")
                        .param("goodsId", goodsId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].value").value("Red"));
    }
}