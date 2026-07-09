package com.teamchallenge.easybuy.shop.dto.shopmoderationhistory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload to reverse moderation action")
public class ShopModerationReversalDTO {

    @NotNull
    @Schema(description = "UserEntity ID who performs reversal", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID reversedByUserId;

    @Size(max = 500)
    @Schema(description = "Reason for reversing moderation action", example = "Appeal approved")
    private String reversalReason;
}

