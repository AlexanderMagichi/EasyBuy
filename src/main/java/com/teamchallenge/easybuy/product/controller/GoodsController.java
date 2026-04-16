package com.teamchallenge.easybuy.product.controller;

import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.entity.category.Category;
import com.teamchallenge.easybuy.product.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Exposes API endpoints in GoodsController.
 */
@RestController
@RequestMapping("/api/goods")
@Tag(name = "Goods", description = "API for managing goods")
public class GoodsController {

    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping
    @Operation(summary = "Get all goods with optional filters", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class)))
    })
    public ResponseEntity<List<GoodsDTO>> getAllGoods(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String art,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) Integer reviewsCount,
            @RequestParam(required = false) UUID shopId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) Goods.GoodsStatus goodsStatus,
            @RequestParam(required = false) Goods.DiscountStatus discountStatus,
            @RequestParam(required = false) Integer rating) {
        // This part creates a temporary Category object just to set its ID.
        // For search filters, it's often cleaner to pass the UUID directly
        // and let the service handle any necessary entity lookups.
        Category categoryFilter = null;
        if (categoryId != null) {
            categoryFilter = new Category();
            categoryFilter.setId(categoryId);
        }

        return ResponseEntity.ok(goodsService.searchGoods(
                id, art, name, price, stock, reviewsCount, shopId,
                categoryFilter, goodsStatus, discountStatus, rating));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goods by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Goods not found")
    })
    public ResponseEntity<GoodsDTO> getGoodsById(@PathVariable UUID id) {
        return ResponseEntity.ok(goodsService.getGoodsById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER','MANAGER','ADMIN')")
    @Operation(summary = "Create a new goods", responses = {
            @ApiResponse(responseCode = "201", description = "Successfully created",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class)))
    })
    public ResponseEntity<GoodsDTO> createGoods(@Valid @RequestBody GoodsDTO dto) { // Added @Valid
        return ResponseEntity.status(201).body(goodsService.createGoods(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER','MANAGER','ADMIN')")
    @Operation(summary = "Update goods", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated",
                    content = @Content(schema = @Schema(implementation = GoodsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Goods not found")
    })
    public ResponseEntity<GoodsDTO> updateGoods(@PathVariable UUID id, @Valid @RequestBody GoodsDTO dto) { // Added @Valid
        return ResponseEntity.ok(goodsService.updateGoods(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER','MANAGER','ADMIN')")
    @Operation(summary = "Delete goods", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Goods not found")
    })
    public ResponseEntity<Void> deleteGoods(@PathVariable UUID id) {
        goodsService.deleteGoods(id);
        return ResponseEntity.ok().build();
    }
}
