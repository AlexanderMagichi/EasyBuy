package com.teamchallenge.easybuy.product.controller.category;

import com.teamchallenge.easybuy.product.dto.category.CategoryDTO;
import com.teamchallenge.easybuy.product.service.category.CategoryService;
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

import java.util.List;
import java.util.UUID;

/**
 * Exposes API endpoints in CategoryController.
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "API for managing categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all categories with optional filters", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    })
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID parentId) {
        if (parentId != null) {
            return ResponseEntity.ok(categoryService.getAllSubcategoriesByParentId(parentId));
        }
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new category", responses = {
            @ApiResponse(responseCode = "201", description = "Successfully created", content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    })
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.status(201).body(categoryService.createCategory(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update category", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = CategoryDTO.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/root")
    @Operation(summary = "Get all root categories", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list", content = @Content(schema = @Schema(implementation = CategoryDTO.class)))
    })
    public ResponseEntity<List<CategoryDTO>> getAllRootCategories() {
        return ResponseEntity.ok(categoryService.getAllRootCategories());
    }
}
