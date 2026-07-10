package com.teamchallenge.easybuy.product.controller.category;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.product.dto.category.CategoryDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.product.exception.CategoryNotFoundException;
import com.teamchallenge.easybuy.product.service.category.CategoryService;
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
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private final UUID id = UUID.randomUUID();
    private final UUID parentId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseCreator()))
                .build();
    }

    @Test
    void getAllCategories_shouldReturnList() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(id);
        dto.setName("Electronics");

        when(categoryService.getAllCategories()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }

    @Test
    void getAllCategories_withParentId_shouldReturnSubcategories() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(id);
        dto.setName("Laptops");

        when(categoryService.getAllSubcategoriesByParentId(parentId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categories").param("parentId", parentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Laptops"));
    }

    @Test
    void getCategoryById_shouldReturnCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(id);
        dto.setName("Electronics");

        when(categoryService.getCategoryById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void getCategoryById_shouldReturn404WhenNotFound() throws Exception {
        when(categoryService.getCategoryById(id))
                .thenThrow(new CategoryNotFoundException(id));

        mockMvc.perform(get("/api/categories/{id}", id))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Category not found with id: " + id.toString()));
    }

    @Test
    void createCategory_shouldReturnCreatedCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");

        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    void updateCategory_shouldReturnUpdatedCategory() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(id);
        dto.setName("Updated Electronics");

        when(categoryService.updateCategory(eq(id), any(CategoryDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Electronics"));
    }

    @Test
    void deleteCategory_shouldReturn200() throws Exception {
        doNothing().when(categoryService).deleteCategory(id);

        mockMvc.perform(delete("/api/categories/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRootCategories_shouldReturnList() throws Exception {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(id);
        dto.setName("Electronics");

        when(categoryService.getAllRootCategories()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categories/root"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Electronics"));
    }
}