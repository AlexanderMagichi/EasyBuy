package com.teamchallenge.easybuy.shop.controller.shopcontactinfo;

import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.shop.service.shopcontactinfo.ShopContactInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Exposes API endpoints in ShopContactInfoController.
 */
@RestController
@RequestMapping("/api/v1/shops/{shopId}/contact-info")
@RequiredArgsConstructor
@Tag(name = "Shop Contact Info", description = "API for managing shop contact information")
public class ShopContactInfoController {

    private final ShopContactInfoService service;

    // ===================== GET =====================
    @Operation(summary = "Get contact info by shop ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact info retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shop not found")
    })
    @GetMapping
    public ResponseEntity<ShopContactInfoDTO> getByShopId(
            @PathVariable @NotNull UUID shopId) {

        return ResponseEntity.ok(service.getByShopId(shopId));
    }

    // ===================== CREATE =====================
    @Operation(summary = "Create contact info for shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contact info created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopContactInfoDTO> create(
            @PathVariable @NotNull UUID shopId,
            @Valid @RequestBody ShopContactInfoDTO dto) {

        ShopContactInfoDTO created = service.create(shopId, dto);

        return ResponseEntity
                .created(URI.create("/api/v1/shops/" + shopId + "/contact-info"))
                .body(created);
    }

    // ===================== UPDATE =====================
    @Operation(summary = "Update contact info (full update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact info updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop not found")
    })
    @PutMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopContactInfoDTO> update(
            @PathVariable @NotNull UUID shopId,
            @Valid @RequestBody ShopContactInfoDTO dto) {

        return ResponseEntity.ok(service.update(shopId, dto));
    }

    // ===================== PATCH =====================
    @Operation(summary = "Partially update contact info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact info patched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop not found")
    })
    @PatchMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopContactInfoDTO> patch(
            @PathVariable @NotNull UUID shopId,
            @RequestBody ShopContactInfoDTO dto) {

        return ResponseEntity.ok(service.patch(shopId, dto));
    }

    // ===================== DELETE =====================
    @Operation(summary = "Deactivate contact info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contact info deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop not found")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(
            @PathVariable @NotNull UUID shopId) {

        service.deactivate(shopId);
        return ResponseEntity.noContent().build();
    }

    // ===================== VERIFY =====================
    @Operation(summary = "Verify contact info (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact info verified successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop not found")
    })
    @PostMapping("/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verify(
            @PathVariable @NotNull UUID shopId) {

        service.verify(shopId);
        return ResponseEntity.ok().build();
    }
}
