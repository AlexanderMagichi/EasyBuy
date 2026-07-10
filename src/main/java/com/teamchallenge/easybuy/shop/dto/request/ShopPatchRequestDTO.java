package com.teamchallenge.easybuy.shop.dto.request;

import com.teamchallenge.easybuy.shop.entity.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

/**
 * Request body for partial shop update.
 */
@Data
@Schema(description = "Request payload for partial shop update")
public class ShopPatchRequestDTO {

    @Size(min = 1, max = 100)
    @Schema(description = "Shop name")
    private String shopName;

    @Size(max = 1000)
    @Schema(description = "Shop description")
    private String shopDescription;

    @Schema(description = "Shop status")
    private Shop.ShopStatus shopStatus;

    @Size(min = 3, max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$")
    @Schema(description = "URL-friendly identifier (slug) of the shop")
    private String slug;

    @Schema(description = "Seller ID (admin-only field)")
    private UUID sellerId;

    @Size(max = 5)
    @Schema(description = "Currency")
    private String currency;

    @Size(max = 5)
    @Schema(description = "Language")
    private String language;

    @Size(max = 50)
    @Schema(description = "Timezone")
    private String timezone;
}

