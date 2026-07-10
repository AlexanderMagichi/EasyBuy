package com.teamchallenge.easybuy.product.controller.category;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.product.dto.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.product.exception.CategoryAttributeException;
import com.teamchallenge.easybuy.product.entity.category.AttributeType;
import com.teamchallenge.easybuy.product.service.category.CategoryAttributeService;
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
class CategoryAttributeControllerTest {

    @Mock
    private CategoryAttributeService categoryAttributeService;

    @InjectMocks
    private CategoryAttributeController categoryAttributeController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private final UUID id = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(categoryAttributeController)
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseCreator()))
                .build();
    }

    @Test
    void getAllAttributes_shouldReturnList() throws Exception {
        CategoryAttributeDTO dto = new CategoryAttributeDTO();
        dto.setId(id);
        dto.setName("Color");
        dto.setType("STRING");
        dto.setCategoryId(categoryId);

        when(categoryAttributeService.getAllAttributes()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/category-attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value("Color"));
    }

    @Test
    void getAttributeById_shouldReturnAttribute() throws Exception {
        CategoryAttributeDTO dto = new CategoryAttributeDTO();
        dto.setId(id);
        dto.setName("Color");

        when(categoryAttributeService.getAttributeById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/category-attributes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Color"));
    }

    @Test
    void getAttributeById_shouldReturn404WhenNotFound() throws Exception {
        when(categoryAttributeService.getAttributeById(id))
                .thenThrow(new CategoryAttributeException("Attribute with ID " + id + " not found"));

        mockMvc.perform(get("/api/category-attributes/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Attribute with ID " + id + " not found"));
    }

    @Test
    void createAttribute_shouldReturnCreatedAttribute() throws Exception {
        CategoryAttributeDTO dto = new CategoryAttributeDTO();
        dto.setName("Color");
        dto.setType("STRING");
        dto.setCategoryId(categoryId);

        when(categoryAttributeService.createAttribute(any(CategoryAttributeDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/category-attributes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Color"));
    }

    @Test
    void updateAttribute_shouldReturnUpdatedAttribute() throws Exception {
        CategoryAttributeDTO dto = new CategoryAttributeDTO();
        dto.setId(id);
        dto.setName("Updated Color");

        when(categoryAttributeService.updateAttribute(eq(id), any(CategoryAttributeDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/category-attributes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Color"));
    }

    @Test
    void deleteAttribute_shouldReturn200() throws Exception {
        doNothing().when(categoryAttributeService).deleteAttribute(id);

        mockMvc.perform(delete("/api/category-attributes/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void searchAttributes_shouldReturnFilteredList() throws Exception {
        CategoryAttributeDTO dto = new CategoryAttributeDTO();
        dto.setId(id);
        dto.setName("Color");

        when(categoryAttributeService.searchAttributes("Color", null, AttributeType.STRING))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/category-attributes/search")
                        .param("name", "Color")
                        .param("type", "STRING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Color"));
    }
}