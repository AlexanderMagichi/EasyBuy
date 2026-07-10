package com.teamchallenge.easybuy.shop.controller.shopmoderationhistory;

import com.teamchallenge.easybuy.shop.dto.shopmoderationhistory.ShopModerationHistoryDTO;
import com.teamchallenge.easybuy.shop.dto.shopmoderationhistory.ShopModerationReversalDTO;
import com.teamchallenge.easybuy.shop.service.shopmoderationhistory.ShopModerationHistoryService;
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
 * Exposes API endpoints in ShopModerationHistoryController.
 */
@RestController
@RequestMapping("/api/v1/shops/{shopId}/moderation-history")
@RequiredArgsConstructor
@Tag(name = "Shop Moderation History", description = "API for shop moderation history records")
public class ShopModerationHistoryController {

    private final ShopModerationHistoryService service;

    @Operation(summary = "Get moderation history for shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moderation history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Shop not found")
    })
    @GetMapping
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<List<ShopModerationHistoryDTO>> getByShopId(@PathVariable @NotNull UUID shopId) {
        return ResponseEntity.ok(service.getByShopId(shopId));
    }

    @Operation(summary = "Get moderation history record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Record or shop not found")
    })
    @GetMapping("/{moderationHistoryId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ShopModerationHistoryDTO> getById(@PathVariable @NotNull UUID shopId,
                                                            @PathVariable @NotNull UUID moderationHistoryId) {
        return ResponseEntity.ok(service.getById(shopId, moderationHistoryId));
    }

    @Operation(summary = "Create moderation history record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopModerationHistoryDTO> create(@PathVariable @NotNull UUID shopId,
                                                           @Valid @RequestBody ShopModerationHistoryDTO dto) {
        ShopModerationHistoryDTO created = service.create(shopId, dto);
        return ResponseEntity
                .created(URI.create("/api/v1/shops/" + shopId + "/moderation-history/" + created.getModerationHistoryId()))
                .body(created);
    }

    @Operation(summary = "Reverse moderation history action")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record reversed successfully"),
            @ApiResponse(responseCode = "400", description = "Action is not reversible or already reversed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Record or shop not found")
    })
    @PostMapping("/{moderationHistoryId}/reverse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShopModerationHistoryDTO> reverse(@PathVariable @NotNull UUID shopId,
                                                            @PathVariable @NotNull UUID moderationHistoryId,
                                                            @Valid @RequestBody ShopModerationReversalDTO dto) {
        return ResponseEntity.ok(service.reverse(shopId, moderationHistoryId, dto));
    }
}

