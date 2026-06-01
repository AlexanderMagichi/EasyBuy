package com.teamchallenge.easybuy.shop.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

/**
 * Request body for shop creation.
 */
@Data
@Schema(description = "Request payload for creating a shop")
public class ShopCreateRequestDTO {

    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "Shop name", example = "MyStore", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @NotBlank
    @Size(max = 1000)
    @Schema(description = "Shop description", example = "Leading seller of shoes and dresses.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopDescription;

    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$")
    @Schema(description = "URL-friendly identifier (slug) of the shop", example = "mystore-shoes")
    private String slug;

    @Schema(description = "Seller ID (required for ADMIN, ignored for SELLER)", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID sellerId;

    @Size(max = 5)
    @Schema(description = "Currency code", example = "UAH")
    private String currency;

    @Size(max = 5)
    @Schema(description = "Language code", example = "uk")
    private String language;

    @Size(max = 50)
    @Schema(description = "Timezone", example = "Europe/Kyiv")
    private String timezone;
}

