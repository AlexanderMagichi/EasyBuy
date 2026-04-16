package com.teamchallenge.easybuy.shop.controller.shopanalytics;

import com.teamchallenge.easybuy.shop.dto.shopanalytics.ShopAnalyticsDTO;
import com.teamchallenge.easybuy.shop.service.shopanalytics.ShopAnalyticsService;
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
import java.util.List;
import java.util.UUID;

/**
 * Exposes API endpoints in ShopAnalyticsController.
 */
@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
@Tag(name = "Shop Analytics", description = "API for shop analytics and dead-shop optimization")
public class ShopAnalyticsController {

    private final ShopAnalyticsService service;

    @Operation(summary = "Get analytics by shop ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shop analytics not found")
    })
    @GetMapping("/{shopId}/analytics")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopAnalyticsDTO> getByShopId(@PathVariable @NotNull UUID shopId) {
        return ResponseEntity.ok(service.getByShopId(shopId));
    }

    @Operation(summary = "Create analytics for shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Analytics created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/{shopId}/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopAnalyticsDTO> create(@PathVariable @NotNull UUID shopId,
                                                   @Valid @RequestBody ShopAnalyticsDTO dto) {
        ShopAnalyticsDTO created = service.create(shopId, dto);
        return ResponseEntity
                .created(URI.create("/api/v1/shops/" + shopId + "/analytics"))
                .body(created);
    }

    @Operation(summary = "Update analytics for shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Shop analytics not found")
    })
    @PutMapping("/{shopId}/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopAnalyticsDTO> update(@PathVariable @NotNull UUID shopId,
                                                   @Valid @RequestBody ShopAnalyticsDTO dto) {
        return ResponseEntity.ok(service.update(shopId, dto));
    }

    @Operation(summary = "Patch analytics for shop")
    @PatchMapping("/{shopId}/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopAnalyticsDTO> patch(@PathVariable @NotNull UUID shopId,
                                                  @Valid @RequestBody ShopAnalyticsDTO dto) {
        return ResponseEntity.ok(service.patch(shopId, dto));
    }

    @Operation(summary = "Recalculate derived analytics metrics")
    @PostMapping("/{shopId}/analytics/recalculate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopAnalyticsDTO> recalculate(@PathVariable @NotNull UUID shopId) {
        return ResponseEntity.ok(service.recalculate(shopId));
    }

    @Operation(summary = "Delete analytics for shop")
    @DeleteMapping("/{shopId}/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NotNull UUID shopId) {
        service.delete(shopId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get dead shops list for optimization")
    @GetMapping("/dead-shops")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShopAnalyticsDTO>> getDeadShops() {
        return ResponseEntity.ok(service.getDeadShops());
    }
}

