package com.teamchallenge.easybuy.product.controller.category;

import com.teamchallenge.easybuy.product.dto.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.product.entity.category.AttributeType;
import com.teamchallenge.easybuy.product.service.category.CategoryAttributeService;
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

/**
 * Exposes API endpoints in CategoryAttributeController.
 */
@RestController
@RequestMapping("/api/category-attributes")
@Tag(name = "Category Attributes", description = "API for managing category attributes")
public class CategoryAttributeController {

    private final CategoryAttributeService categoryAttributeService;

    @Autowired
    public CategoryAttributeController(CategoryAttributeService categoryAttributeService) {
        this.categoryAttributeService = categoryAttributeService;
    }

    @GetMapping
    @Operation(summary = "Get all category attributes", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(schema = @Schema(implementation = CategoryAttributeDTO.class)))
    })
    public ResponseEntity<List<CategoryAttributeDTO>> getAllAttributes() {
        return ResponseEntity.ok(categoryAttributeService.getAllAttributes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category attribute by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = CategoryAttributeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Attribute not found")
    })
    public ResponseEntity<CategoryAttributeDTO> getAttributeById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryAttributeService.getAttributeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new category attribute", responses = {
            @ApiResponse(responseCode = "201", description = "Successfully created", content = @Content(schema = @Schema(implementation = CategoryAttributeDTO.class)))
    })
    public ResponseEntity<CategoryAttributeDTO> createAttribute(@RequestBody CategoryAttributeDTO dto) {
        return ResponseEntity.status(201).body(categoryAttributeService.createAttribute(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a category attribute", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = CategoryAttributeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Attribute not found")
    })
    public ResponseEntity<CategoryAttributeDTO> updateAttribute(@PathVariable UUID id, @RequestBody CategoryAttributeDTO dto) {
        return ResponseEntity.ok(categoryAttributeService.updateAttribute(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a category attribute", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Attribute not found")
    })
    public ResponseEntity<Void> deleteAttribute(@PathVariable UUID id) {
        categoryAttributeService.deleteAttribute(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search category attributes by name, category ID, or type", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(schema = @Schema(implementation = CategoryAttributeDTO.class)))
    })
    public ResponseEntity<List<CategoryAttributeDTO>> searchAttributes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) AttributeType type) {
        return ResponseEntity.ok(categoryAttributeService.searchAttributes(name, categoryId, type));
    }
}
