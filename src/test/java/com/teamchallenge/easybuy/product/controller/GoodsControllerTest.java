package com.teamchallenge.easybuy.product.controller;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import com.teamchallenge.easybuy.infrastructure.exception.handler.GlobalExceptionHandler;
import com.teamchallenge.easybuy.product.exception.GoodsNotFoundException;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.service.GoodsService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GoodsControllerTest {

    @Mock
    private GoodsService goodsService; // Mock the GoodsService dependency

    @InjectMocks
    private GoodsController goodsController; // Inject mocks into the controller under test

    private MockMvc mockMvc; // Used to simulate HTTP requests

    private ObjectMapper objectMapper; // Helper for JSON serialization/deserialization

    private UUID id;
    private UUID categoryId;
    private UUID shopId; // Added for completeness in DTO setup

    @BeforeEach
    void setUp() {
        // Initialize ObjectMapper and MockMvc before each test
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(goodsController)
                .setControllerAdvice(new GlobalExceptionHandler(new ApiErrorResponseCreator())) // Apply global exception handler
                .build();

        // Initialize UUIDs for consistent testing
        id = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        shopId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /api/goods should return a list of goods with applied filters")
    void getAllGoods_shouldReturnListWithFilters() throws Exception {
        // Arrange: Prepare a GoodsDTO for the mock service response
        GoodsDTO dto = new GoodsDTO();
        dto.setId(id);
        dto.setName("Wireless Mouse");

        // Mock the goodsService.searchGoods to return our prepared list
        // `any()` is used for all parameters because the controller passes them directly from request params.
        when(goodsService.searchGoods(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(dto));

        // Act & Assert: Perform GET request with a filter and verify the response
        mockMvc.perform(get("/api/goods")
                        .param("name", "Wireless Mouse")) // Apply a 'name' filter
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$", hasSize(1))) // Expect a list with one item
                .andExpect(jsonPath("$[0].name").value("Wireless Mouse")); // Verify the name of the first item
    }

    @Test
    @DisplayName("GET /api/goods/{id} should return goods by ID")
    void getGoodsById_shouldReturnGoods() throws Exception {
        // Arrange: Prepare a GoodsDTO for the mock service response
        GoodsDTO dto = new GoodsDTO();
        dto.setId(id);
        dto.setName("Wireless Mouse");

        // Mock the goodsService.getGoodsById to return our prepared DTO when called with `id`
        when(goodsService.getGoodsById(id)).thenReturn(dto);

        // Act & Assert: Perform GET request by ID and verify the response
        mockMvc.perform(get("/api/goods/{id}", id))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(id.toString())) // Verify the ID in the JSON response
                .andExpect(jsonPath("$.name").value("Wireless Mouse")); // Verify the name in the JSON response
    }

    @Test
    @DisplayName("GET /api/goods/{id} should return 404 when goods not found")
    void getGoodsById_shouldReturn404WhenNotFound() throws Exception {
        // Arrange: Mock the goodsService.getGoodsById to throw GoodsNotFoundException
        when(goodsService.getGoodsById(id))
                .thenThrow(new GoodsNotFoundException(id));

        // Act & Assert: Perform GET request and expect 404 Not Found
        mockMvc.perform(get("/api/goods/{id}", id))
                .andExpect(status().isNotFound()) // Expect HTTP 404 Not Found
                .andExpect(jsonPath("$.status").value(404)); // Verify the status code in the error response
    }

    @Test
    @DisplayName("POST /api/goods should create and return new goods")
    void createGoods_shouldReturnCreatedGoods() throws Exception {
        // Arrange: Prepare a GoodsDTO for the request body and the mock service response
        GoodsDTO requestDto = new GoodsDTO();
        requestDto.setName("New Wireless Mouse");
        requestDto.setArt("ART-002");
        requestDto.setDescription("New description");
        requestDto.setPrice(new BigDecimal("120.00"));
        requestDto.setMainImageUrl("https://example.com/new-mouse.jpg");
        requestDto.setStock(200);
        requestDto.setReviewsCount(0);
        requestDto.setShopId(shopId); // Use generated shopId
        requestDto.setCategoryId(categoryId); // Use generated categoryId
        requestDto.setGoodsStatus(Goods.GoodsStatus.ACTIVE); // Use enum value
        requestDto.setDiscountStatus(Goods.DiscountStatus.NONE); // Use enum value
        requestDto.setDiscountValue(null);
        requestDto.setRating(0);
        requestDto.setSlug("new-wireless-mouse");
        requestDto.setMetaTitle("New Mouse Title");
        requestDto.setMetaDescription("New Mouse Description");
        requestDto.setAdditionalImages(List.of()); // Assuming this is present

        // Create a DTO that will be returned by the service (simulating successful creation)
        GoodsDTO responseDto = new GoodsDTO();
        responseDto.setId(UUID.randomUUID()); // A newly generated ID
        responseDto.setName(requestDto.getName());
        responseDto.setArt(requestDto.getArt());
        responseDto.setPrice(requestDto.getPrice());
        responseDto.setMainImageUrl(requestDto.getMainImageUrl());
        responseDto.setStock(requestDto.getStock());
        responseDto.setShopId(requestDto.getShopId());
        responseDto.setCategoryId(requestDto.getCategoryId());
        responseDto.setGoodsStatus(requestDto.getGoodsStatus());
        responseDto.setDiscountStatus(requestDto.getDiscountStatus());

        // Mock the goodsService.createGoods to return our prepared response DTO
        when(goodsService.createGoods(any(GoodsDTO.class))).thenReturn(responseDto);

        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/api/goods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))) // Convert DTO to JSON string
                .andExpect(status().isCreated()) // Expect HTTP 201 Created
                .andExpect(jsonPath("$.name").value("New Wireless Mouse")) // Verify name
                .andExpect(jsonPath("$.art").value("ART-002")) // Verify art
                .andExpect(jsonPath("$.id").exists()); // Verify that an ID was assigned
    }

    @Test
    @DisplayName("PUT /api/goods/{id} should update and return updated goods")
    void updateGoods_shouldReturnUpdatedGoods() throws Exception {
        // Arrange: Prepare a GoodsDTO for the request body and the mock service response
        GoodsDTO requestDto = new GoodsDTO();
        requestDto.setId(id); // ID in DTO matches path variable
        requestDto.setName("Updated Mouse Name");
        requestDto.setArt("ART-001"); // Keep original art or change as needed
        requestDto.setPrice(new BigDecimal("150.00"));
        requestDto.setMainImageUrl("https://example.com/updated-mouse.jpg");
        requestDto.setStock(150);
        requestDto.setShopId(shopId);
        requestDto.setCategoryId(categoryId);
        requestDto.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        requestDto.setDiscountStatus(Goods.DiscountStatus.NONE);

        // Mock the goodsService.updateGoods to return our prepared DTO
        when(goodsService.updateGoods(eq(id), any(GoodsDTO.class))).thenReturn(requestDto);

        // Act & Assert: Perform PUT request and verify the response
        mockMvc.perform(put("/api/goods/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))) // Convert DTO to JSON string
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id").value(id.toString())) // Verify ID
                .andExpect(jsonPath("$.name").value("Updated Mouse Name")); // Verify updated name
    }

    @Test
    @DisplayName("DELETE /api/goods/{id} should delete goods successfully")
    void deleteGoods_shouldReturn200() throws Exception {
        // Arrange: Mock the goodsService.deleteGoods to do nothing (successful deletion)
        doNothing().when(goodsService).deleteGoods(id);

        // Act & Assert: Perform DELETE request and expect 200 OK
        mockMvc.perform(delete("/api/goods/{id}", id))
                .andExpect(status().isOk()); // Expect HTTP 200 OK
    }

    @Test
    @DisplayName("POST /api/goods should return 400 when invalid DTO is provided")
    void createGoods_shouldReturn400WhenInvalid() throws Exception {
        // Arrange: Prepare a GoodsDTO with invalid data to trigger validation errors
        GoodsDTO dto = new GoodsDTO();
        dto.setName(null); // Violates @NotNull
        dto.setArt(null);  // Violates @NotNull
        dto.setPrice(new BigDecimal("-10")); // Violates @Positive
        dto.setMainImageUrl("invalid-url");  // Violates @Pattern (if it exists)
        dto.setStock(-5);  // Violates @PositiveOrZero
        dto.setShopId(null); // Violates @NotNull
        dto.setCategoryId(null); // Violates @NotNull (assuming CategoryId is @NotNull in GoodsDTO)
        dto.setGoodsStatus(null); // Violates @NotNull
        dto.setDiscountStatus(null); // Violates @NotNull

        // Act & Assert: Perform POST request with invalid DTO and expect 400 Bad Request
        mockMvc.perform(post("/api/goods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()) // Expect HTTP 400 Bad Request
                // Verify specific error messages for each violated constraint
                .andExpect(jsonPath("$.errors.name").value("must not be null"))
                .andExpect(jsonPath("$.errors.art").value("must not be null"))
                .andExpect(jsonPath("$.errors.price").value("Price must be greater than 0"))
                .andExpect(jsonPath("$.errors.stock").value("must be greater than or equal to 0"))
                .andExpect(jsonPath("$.errors.shopId").value("must not be null"))
                .andExpect(jsonPath("$.errors.categoryId").value("must not be null")) // Assuming categoryId is @NotNull
                .andExpect(jsonPath("$.errors.goodsStatus").value("must not be null"))
                .andExpect(jsonPath("$.errors.discountStatus").value("must not be null"));
        // Add more checks for mainImageUrl if it has @Pattern validation
        // .andExpect(jsonPath("$.errors.mainImageUrl").value("must match \"^(http|https)://.*\"")); // Example for @Pattern
    }
}