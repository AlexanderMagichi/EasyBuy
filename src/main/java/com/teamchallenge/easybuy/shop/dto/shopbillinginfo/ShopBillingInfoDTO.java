package com.teamchallenge.easybuy.shop.dto.shopbillinginfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Stripe billing information and onboarding status")
public class ShopBillingInfoDTO {

    @Schema(description = "The ID of the billing info (matches Shop ID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "The unique Stripe Account ID", example = "acct_1PC1234567890ABC", accessMode = Schema.AccessMode.READ_ONLY)
    private String stripeAccountId;

    @Schema(description = "Indicates if the seller can receive payouts", example = "false")
    private Boolean payoutsEnabled;

    @Email(message = "Invalid billing email format")
    @Schema(description = "Primary email for financial notifications", example = "finance@shop.com")
    private String billingEmail;

    @Schema(description = "Default settlement currency (ISO code)", example = "USD")
    private String defaultCurrency;

    @Schema(description = "Temporary URL for Stripe onboarding", accessMode = Schema.AccessMode.READ_ONLY)
    private String onboardingUrl;
}