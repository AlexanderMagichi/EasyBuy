package com.teamchallenge.easybuy.shop.dto.request;

import com.teamchallenge.easybuy.shop.entity.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

/**
 * Request body for full shop update.
 */
@Data
@Schema(description = "Request payload for full shop update")
public class ShopUpdateRequestDTO {

    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(description = "Shop name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopName;

    @NotBlank
    @Size(max = 1000)
    @Schema(description = "Shop description", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopDescription;

    @Schema(description = "Shop status")
    private Shop.ShopStatus shopStatus;

    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$")
    @Schema(description = "URL-friendly identifier (slug) of the shop")
    private String slug;

    @Schema(description = "Seller ID (admin-only field)")
    private UUID sellerId;

    @NotBlank
    @Size(max = 5)
    @Schema(description = "Currency", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @NotBlank
    @Size(max = 5)
    @Schema(description = "Language", requiredMode = Schema.RequiredMode.REQUIRED)
    private String language;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Timezone", requiredMode = Schema.RequiredMode.REQUIRED)
    private String timezone;
}

