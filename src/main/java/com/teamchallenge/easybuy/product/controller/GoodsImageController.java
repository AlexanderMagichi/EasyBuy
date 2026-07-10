package com.teamchallenge.easybuy.product.controller;

import com.teamchallenge.easybuy.product.dto.GoodsImageDTO;
import com.teamchallenge.easybuy.product.service.image.GoodsImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Exposes API endpoints in GoodsImageController.
 */
@RestController
@RequestMapping("/api/goods-images")
@Tag(name = "Goods Images", description = "API for managing goods images")
public class GoodsImageController {

    private final GoodsImageService goodsImageService;

    @Autowired
    public GoodsImageController(GoodsImageService goodsImageService) {
        this.goodsImageService = goodsImageService;
    }

    @GetMapping
    @Operation(summary = "Get all goods images", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = GoodsImageDTO.class)))
    })
    public ResponseEntity<List<GoodsImageDTO>> getAllImages() {
        return ResponseEntity.ok(goodsImageService.getAllImages());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get goods image by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved",
                    content = @Content(schema = @Schema(implementation = GoodsImageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<GoodsImageDTO> getImageById(@PathVariable UUID id) {
        return ResponseEntity.ok(goodsImageService.getImageById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER','MANAGER','ADMIN')")
    @Operation(summary = "Create a new goods image", responses = {
            @ApiResponse(responseCode = "201", description = "Successfully created",
                    content = @Content(schema = @Schema(implementation = GoodsImageDTO.class)))
    })
    public ResponseEntity<GoodsImageDTO> createImage(
            @RequestParam("goodsId") UUID goodsId,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).body(goodsImageService.createImage(goodsId, file));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER','MANAGER','ADMIN')")
    @Operation(summary = "Update a goods image", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated",
                    content = @Content(schema = @Schema(implementation = GoodsImageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<GoodsImageDTO> updateImage(
            @PathVariable UUID id,
            @RequestParam("goodsId") UUID goodsId,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(goodsImageService.updateImage(id, goodsId, file));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER','MANAGER','ADMIN')")
    @Operation(summary = "Delete a goods image", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id) throws IOException {
        goodsImageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search goods images by goods ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = GoodsImageDTO.class)))
    })
    public ResponseEntity<List<GoodsImageDTO>> searchImages(@RequestParam(required = false) UUID goodsId) {
        return ResponseEntity.ok(goodsImageService.searchImages(goodsId));
    }
}
