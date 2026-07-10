package com.teamchallenge.easybuy.shop.controller.shopseosettings;

import com.teamchallenge.easybuy.shop.dto.ShopSeoSettingsDTO;
import com.teamchallenge.easybuy.shop.service.shopseosettings.ShopSeoSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Exposes API endpoints in ShopSeoSettingsController.
 */
@RestController
@RequestMapping("/api/v1/shops/{shopId}/seo-settings")
@RequiredArgsConstructor
@Tag(name = "Shop SEO Settings", description = "API for managing shop SEO settings")
public class ShopSeoSettingsController {

    private final ShopSeoSettingsService service;

    @Operation(summary = "Get SEO settings by shop ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SEO settings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shop or SEO settings not found")
    })
    @GetMapping
    public ResponseEntity<ShopSeoSettingsDTO> getByShopId(@PathVariable @NotNull UUID shopId) {
        return ResponseEntity.ok(service.getByShopId(shopId));
    }

    @Operation(summary = "Create SEO settings for shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "SEO settings created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopSeoSettingsDTO> create(@PathVariable @NotNull UUID shopId,
                                                     @Valid @RequestBody ShopSeoSettingsDTO dto) {
        ShopSeoSettingsDTO created = service.create(shopId, dto);
        return ResponseEntity
                .created(URI.create("/api/v1/shops/" + shopId + "/seo-settings"))
                .body(created);
    }

    @Operation(summary = "Update SEO settings (full update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SEO settings updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop or SEO settings not found")
    })
    @PutMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopSeoSettingsDTO> update(@PathVariable @NotNull UUID shopId,
                                                     @Valid @RequestBody ShopSeoSettingsDTO dto) {
        return ResponseEntity.ok(service.update(shopId, dto));
    }

    @Operation(summary = "Partially update SEO settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SEO settings patched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop or SEO settings not found")
    })
    @PatchMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopSeoSettingsDTO> patch(@PathVariable @NotNull UUID shopId,
                                                    @RequestBody ShopSeoSettingsDTO dto) {
        return ResponseEntity.ok(service.patch(shopId, dto));
    }

    @Operation(summary = "Delete SEO settings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "SEO settings deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop or SEO settings not found")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NotNull UUID shopId) {
        service.delete(shopId);
        return ResponseEntity.noContent().build();
    }
}

