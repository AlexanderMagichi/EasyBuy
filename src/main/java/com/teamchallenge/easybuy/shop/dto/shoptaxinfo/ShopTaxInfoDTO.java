package com.teamchallenge.easybuy.shop.dto.shoptaxinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Legal and tax information of the shop")
public class ShopTaxInfoDTO {

    @Schema(description = "Shop ID (matches Billing and Shop IDs)", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotBlank(message = "Tax ID is required")
    @Schema(description = "TIN, EIN, or VAT identification number", example = "1234567890")
    private String taxId;

    @NotNull(message = "Taxpayer type is required")
    @Schema(description = "Entity legal form", example = "BUSINESS")
    private String taxpayerType;

    @NotBlank(message = "Legal name is required")
    @Schema(description = "Official business name", example = "EasyBuy Solutions LLC")
    private String legalName;

    @Schema(description = "ISO country code for tax residence", example = "US")
    private String taxCountryCode;

    @Schema(description = "Official registered business address")
    private String registeredAddress;
}