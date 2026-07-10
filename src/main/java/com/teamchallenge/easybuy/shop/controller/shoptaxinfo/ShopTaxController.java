package com.teamchallenge.easybuy.shop.controller.shoptaxinfo;

import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import com.teamchallenge.easybuy.shop.service.shoptaxinfo.ShopTaxService;
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
 * Exposes API endpoints in ShopTaxController.
 */
@RestController
@RequestMapping("/api/v1/shops/{shopId}/tax-info")
@RequiredArgsConstructor
@Tag(name = "Shop Tax Info", description = "API for managing shop tax and legal information")
public class ShopTaxController {

    private final ShopTaxService service;

    // ===================== GET =====================
    @Operation(summary = "Get tax info by shop ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tax info retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shop or tax info not found")
    })
    @GetMapping
    public ResponseEntity<ShopTaxInfoDTO> getByShopId(@PathVariable @NotNull UUID shopId) {
        return ResponseEntity.ok(service.getByShopId(shopId));
    }

    // ===================== CREATE =====================
    @Operation(summary = "Create tax info for shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tax info created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopTaxInfoDTO> create(@PathVariable @NotNull UUID shopId,
                                                 @Valid @RequestBody ShopTaxInfoDTO dto) {
        ShopTaxInfoDTO created = service.create(shopId, dto);
        return ResponseEntity
                .created(URI.create("/api/v1/shops/" + shopId + "/tax-info"))
                .body(created);
    }

    // ===================== UPDATE =====================
    @Operation(summary = "Update tax info (full update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tax info updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop or tax info not found")
    })
    @PutMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopTaxInfoDTO> update(@PathVariable @NotNull UUID shopId,
                                                 @Valid @RequestBody ShopTaxInfoDTO dto) {
        return ResponseEntity.ok(service.update(shopId, dto));
    }

    // ===================== PATCH =====================
    @Operation(summary = "Partially update tax info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tax info patched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop or tax info not found")
    })
    @PatchMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopTaxInfoDTO> patch(@PathVariable @NotNull UUID shopId,
                                                @RequestBody ShopTaxInfoDTO dto) {
        return ResponseEntity.ok(service.patch(shopId, dto));
    }

    // ===================== DELETE =====================
    @Operation(summary = "Delete tax info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tax info deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop or tax info not found")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NotNull UUID shopId) {
        service.delete(shopId);
        return ResponseEntity.noContent().build();
    }
}

