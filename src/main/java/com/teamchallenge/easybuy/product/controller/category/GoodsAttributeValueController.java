package com.teamchallenge.easybuy.product.controller.category;

import com.teamchallenge.easybuy.product.dto.category.GoodsAttributeValueDTO;
import com.teamchallenge.easybuy.product.service.category.GoodsAttributeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// todo Note: Caching is implemented at the service level. To control the cache on the client side, you can add @CacheControl.
/**
 * Exposes API endpoints in GoodsAttributeValueController.
 */
@RestController
@RequestMapping("/api/attribute-values")
@Tag(name = "Goods Attribute Values", description = "API for managing goods attribute values")
public class GoodsAttributeValueController {

    private final GoodsAttributeValueService goodsAttributeValueService;

    @Autowired
    public GoodsAttributeValueController(GoodsAttributeValueService goodsAttributeValueService) {
        this.goodsAttributeValueService = goodsAttributeValueService;
    }

    @GetMapping
    @Operation(summary = "Get all attribute values", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(schema = @Schema(implementation = GoodsAttributeValueDTO.class)))
    })
    public ResponseEntity<List<GoodsAttributeValueDTO>> getAllAttributeValues() {
        return ResponseEntity.ok(goodsAttributeValueService.getAllAttributeValues());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attribute value by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = GoodsAttributeValueDTO.class))),
            @ApiResponse(responseCode = "404", description = "Attribute value not found")
    })
    public ResponseEntity<GoodsAttributeValueDTO> getAttributeValueById(@PathVariable UUID id) {
        return ResponseEntity.ok(goodsAttributeValueService.getAttributeValueById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new attribute value", responses = {
            @ApiResponse(responseCode = "201", description = "Successfully created", content = @Content(schema = @Schema(implementation = GoodsAttributeValueDTO.class)))
    })
    public ResponseEntity<GoodsAttributeValueDTO> createAttributeValue(@RequestBody GoodsAttributeValueDTO dto) {
        return ResponseEntity.status(201).body(goodsAttributeValueService.createAttributeValue(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an attribute value", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = GoodsAttributeValueDTO.class))),
            @ApiResponse(responseCode = "404", description = "Attribute value not found")
    })
    public ResponseEntity<GoodsAttributeValueDTO> updateAttributeValue(@PathVariable UUID id, @RequestBody GoodsAttributeValueDTO dto) {
        return ResponseEntity.ok(goodsAttributeValueService.updateAttributeValue(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an attribute value", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Attribute value not found")
    })
    public ResponseEntity<Void> deleteAttributeValue(@PathVariable UUID id) {
        goodsAttributeValueService.deleteAttributeValue(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search attribute values by goods ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(schema = @Schema(implementation = GoodsAttributeValueDTO.class)))
    })
    public ResponseEntity<List<GoodsAttributeValueDTO>> searchAttributeValues(@RequestParam(required = false) UUID goodsId) {
        return ResponseEntity.ok(goodsAttributeValueService.searchAttributeValues(goodsId));
    }
}
