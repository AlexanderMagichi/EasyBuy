package com.teamchallenge.easybuy.shop.controller;

import com.teamchallenge.easybuy.shop.dto.ShopDTO;
import com.teamchallenge.easybuy.shop.dto.ShopSearchParams;
import com.teamchallenge.easybuy.shop.dto.request.ShopCreateRequestDTO;
import com.teamchallenge.easybuy.shop.dto.request.ShopPatchRequestDTO;
import com.teamchallenge.easybuy.shop.dto.request.ShopUpdateRequestDTO;
import com.teamchallenge.easybuy.shop.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * Exposes API endpoints in ShopController.
 */
@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
@Tag(name = "Shop Management", description = "API for managing shops")
public class ShopController {

    private final ShopService shopService;

    // ===================== GET ALL =====================

    @Operation(summary = "Get all shops")
    @GetMapping
    public ResponseEntity<Page<ShopDTO>> getAllShops(
            @PageableDefault(size = 20)
            @Parameter(hidden = true)
            Pageable pageable) {

        return ResponseEntity.ok(shopService.getAllShops(pageable));
    }

    // ===================== GET BY ID =====================

    @Operation(summary = "Get shop by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ShopDTO> getShopById(
            @PathVariable @NotNull UUID id) {

        return ResponseEntity.ok(shopService.getShopById(id));
    }

    // ===================== CREATE =====================

    @Operation(summary = "Create a new shop")
    @PostMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> createShop(
            @Valid @RequestBody ShopCreateRequestDTO request) {

        ShopDTO shopDTO = mapCreateRequestToShopDto(request);

        ShopDTO created = shopService.createShop(shopDTO);

        return ResponseEntity
                .created(URI.create("/api/v1/shops/" + created.getShopId()))
                .body(created);
    }

    // ===================== UPDATE =====================

    @Operation(summary = "Update shop (full update)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> updateShop(
            @PathVariable @NotNull UUID id,
            @Valid @RequestBody ShopUpdateRequestDTO request) {

        ShopDTO shopDTO = mapUpdateRequestToShopDto(request);

        return ResponseEntity.ok(shopService.updateShop(id, shopDTO));
    }

    // ===================== PATCH =====================

    @Operation(summary = "Partially update shop")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> patchShop(
            @PathVariable @NotNull UUID id,
            @RequestBody ShopPatchRequestDTO request) {

        ShopDTO shopDTO = mapPatchRequestToShopDto(request);

        return ResponseEntity.ok(shopService.patchShop(id, shopDTO));
    }

    @Operation(summary = "Update shop profile in one request",
            description = "Updates base shop data and nested contact/tax/seo blocks for single-page shop settings UI")
    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopDTO> updateShopProfile(
            @PathVariable @NotNull UUID id,
            @RequestBody ShopDTO shopDTO) {

        return ResponseEntity.ok(shopService.updateShopProfile(id, shopDTO));
    }

    // ===================== DELETE =====================

    @Operation(summary = "Delete shop")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShop(
            @PathVariable @NotNull UUID id) {

        shopService.deleteShop(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== SEARCH =====================

    @Operation(summary = "Search shops with filters")
    @GetMapping("/search")
    public ResponseEntity<Page<ShopDTO>> searchShops(
            @ModelAttribute ShopSearchParams params,
            @PageableDefault(size = 20)
            @Parameter(hidden = true)
            Pageable pageable) {

        return ResponseEntity.ok(shopService.searchShops(params, pageable));
    }

    private ShopDTO mapCreateRequestToShopDto(ShopCreateRequestDTO request) {
        ShopDTO dto = new ShopDTO();
        dto.setShopName(request.getShopName());
        dto.setShopDescription(request.getShopDescription());
        dto.setSellerId(request.getSellerId());
        dto.setCurrency(request.getCurrency());
        dto.setLanguage(request.getLanguage());
        dto.setTimezone(request.getTimezone());
        return dto;
    }

    private ShopDTO mapUpdateRequestToShopDto(ShopUpdateRequestDTO request) {
        ShopDTO dto = new ShopDTO();
        dto.setShopName(request.getShopName());
        dto.setShopDescription(request.getShopDescription());
        dto.setShopStatus(request.getShopStatus());
        dto.setSellerId(request.getSellerId());
        dto.setCurrency(request.getCurrency());
        dto.setLanguage(request.getLanguage());
        dto.setTimezone(request.getTimezone());
        return dto;
    }

    private ShopDTO mapPatchRequestToShopDto(ShopPatchRequestDTO request) {
        ShopDTO dto = new ShopDTO();
        dto.setShopName(request.getShopName());
        dto.setShopDescription(request.getShopDescription());
        dto.setShopStatus(request.getShopStatus());
        dto.setSellerId(request.getSellerId());
        dto.setCurrency(request.getCurrency());
        dto.setLanguage(request.getLanguage());
        dto.setTimezone(request.getTimezone());
        return dto;
    }
}
