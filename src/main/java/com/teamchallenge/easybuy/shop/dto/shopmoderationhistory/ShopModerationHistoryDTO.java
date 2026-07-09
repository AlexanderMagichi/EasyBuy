package com.teamchallenge.easybuy.shop.dto.shopmoderationhistory;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopModerationHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO for shop moderation history")
public class ShopModerationHistoryDTO {

    @Schema(description = "Moderation history record ID", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID moderationHistoryId;

    @Schema(description = "Shop ID", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID shopId;

    @NotNull
    @Schema(description = "Moderator user ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID moderatorId;

    @NotNull
    @Schema(description = "Moderation action type", requiredMode = Schema.RequiredMode.REQUIRED)
    private ShopModerationHistory.ModerationActionType actionType;

    @Schema(description = "Previous shop status")
    private Shop.ShopStatus previousStatus;

    @Schema(description = "New shop status")
    private Shop.ShopStatus newStatus;

    @Size(max = 1000)
    @Schema(description = "Reason for moderation action")
    private String reason;

    @Size(max = 2000)
    @Schema(description = "Detailed moderation notes")
    private String details;

    @Size(max = 1000)
    @Schema(description = "Evidence for moderation decision")
    private String evidence;

    @Schema(description = "Duration of action in days")
    private Integer durationDays;

    @Schema(description = "Expiration timestamp for temporary actions")
    private Instant expiresAt;

    @Schema(description = "Whether action was automatic")
    private Boolean automatic;

    @Schema(description = "Whether action requires follow-up")
    private Boolean requiresFollowUp;

    @Schema(description = "Follow-up date")
    private Instant followUpDate;

    @Size(max = 500)
    @Schema(description = "Follow-up notes")
    private String followUpNotes;

    @Schema(description = "Severity level of action")
    private ShopModerationHistory.SeverityLevel severityLevel;

    @Schema(description = "Whether action is reversible")
    private Boolean reversible;

    @Schema(description = "When action was reversed", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant reversedAt;

    @Schema(description = "UserEntity who reversed action")
    private UUID reversedByUserId;

    @Size(max = 500)
    @Schema(description = "Reason for reversing moderation action")
    private String reversalReason;

    @Schema(description = "Calculated impact score", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer impactScore;

    @Size(max = 1000)
    @Schema(description = "JSON tags for action categorization")
    private String tags;

    @Schema(description = "Creation time", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;
}

