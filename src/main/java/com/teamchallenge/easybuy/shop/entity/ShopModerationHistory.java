package com.teamchallenge.easybuy.shop.entity;

import com.teamchallenge.easybuy.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shop_moderation_history", indexes = {
        @Index(name = "idx_moderation_shop_id", columnList = "shop_id"),
        @Index(name = "idx_moderation_moderator_id", columnList = "moderator_id"),
        @Index(name = "idx_moderation_action_type", columnList = "action_type"),
        @Index(name = "idx_moderation_created_at", columnList = "created_at")
})
/**
 * Represents the ShopModerationHistory persistence entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "History of shop moderation actions")
public class ShopModerationHistory {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "moderation_history_id", nullable = false, updatable = false)
    @Schema(description = "Unique moderation history record ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID moderationHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @NotNull
    @Schema(description = "Shop that was moderated", requiredMode = Schema.RequiredMode.REQUIRED)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id", nullable = false)
    @NotNull
    @Schema(description = "Admin/moderator who performed the action", requiredMode = Schema.RequiredMode.REQUIRED)
    private User moderator;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    @Schema(description = "Type of moderation action performed", example = "STATUS_CHANGE", requiredMode = Schema.RequiredMode.REQUIRED)
    private ModerationActionType actionType;
    @Column(name = "previous_status")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Previous shop status before action", example = "PENDING")
    private Shop.ShopStatus previousStatus;
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    @Schema(description = "New shop status after action", example = "ACTIVE")
    private Shop.ShopStatus newStatus;
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    @Column(name = "reason", columnDefinition = "TEXT")
    @Schema(description = "Reason for the moderation action", example = "Shop verified successfully after document review")
    private String reason;
    @Size(max = 2000, message = "Details must not exceed 2000 characters")
    @Column(name = "details", columnDefinition = "TEXT")
    @Schema(description = "Detailed description of the action taken",
            example = "All required documents were verified and approved")
    private String details;
    @Size(max = 1000, message = "Evidence must not exceed 1000 characters")
    @Column(name = "evidence", columnDefinition = "TEXT")
    @Schema(description = "Evidence or proof for the action", example = "Document IDs: DOC123, DOC456")
    private String evidence;
    @Column(name = "duration_days")
    @Schema(description = "Duration of action in days (for suspensions, bans, etc.)", example = "30")
    private Integer durationDays;
    @Column(name = "expires_at")
    @Schema(description = "When the action expires (for temporary actions)", example = "2025-02-15T10:30:00Z")
    private Instant expiresAt;
    @Column(name = "is_automatic", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the action was performed automatically by system", example = "false")
    private boolean isAutomatic = false;
    @Column(name = "requires_follow_up", nullable = false)
    @Builder.Default
    @Schema(description = "Whether this action requires follow-up", example = "false")
    private boolean requiresFollowUp = false;
    @Column(name = "follow_up_date")
    @Schema(description = "Date when follow-up is required", example = "2025-02-15T10:30:00Z")
    private Instant followUpDate;
    @Size(max = 500, message = "Follow-up notes must not exceed 500 characters")
    @Column(name = "follow_up_notes", length = 500)
    @Schema(description = "Notes for follow-up action", example = "Check shop compliance in 30 days")
    private String followUpNotes;
    @Column(name = "severity_level")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Severity level of the action", example = "MEDIUM")
    private SeverityLevel severityLevel;
    @Column(name = "is_reversible", nullable = false)
    @Builder.Default
    @Schema(description = "Whether this action can be reversed", example = "true")
    private boolean isReversible = true;
    @Column(name = "reversed_at")
    @Schema(description = "When this action was reversed", example = "2025-02-15T10:30:00Z")
    private Instant reversedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversed_by_user_id")
    @Schema(description = "User who reversed this action")
    private User reversedByUser;
    @Size(max = 500, message = "Reversal reason must not exceed 500 characters")
    @Column(name = "reversal_reason", length = 500)
    @Schema(description = "Reason for reversing the action", example = "Appeal approved")
    private String reversalReason;
    @Column(name = "impact_score")
    @Schema(description = "Impact score of the action (0-100)", example = "75")
    private Integer impactScore;
    @Size(max = 1000, message = "Tags must not exceed 1000 characters")
    @Column(name = "tags", columnDefinition = "TEXT")
    @Schema(description = "Tags for categorization (JSON array)", example = "[\"verification\", \"compliance\", \"urgent\"]")
    private String tags;
    @Column(name = "created_at", updatable = false)
    @Schema(description = "When the moderation action was performed", example = "2025-01-15T10:30:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    /**
     * Check if the action is still active (not expired)
     */
    public boolean isActive() {
        if (expiresAt == null) {
            return true; // Permanent action
        }
        return Instant.now().isBefore(expiresAt);
    }

    /**
     * Check if the action is expired
     */
    public boolean isExpired() {
        return !isActive();
    }

    // --- Business methods ---

    /**
     * Check if follow-up is required and overdue
     */
    public boolean isFollowUpOverdue() {
        return requiresFollowUp && followUpDate != null && Instant.now().isAfter(followUpDate);
    }

    /**
     * Get action summary
     */
    public String getActionSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(actionType.name());

        if (previousStatus != null && newStatus != null) {
            summary.append(": ").append(previousStatus).append(" в†’ ").append(newStatus);
        }

        if (reason != null && !reason.trim().isEmpty()) {
            summary.append(" - ").append(reason);
        }

        return summary.toString();
    }

    /**
     * Check if action affects shop status
     */
    public boolean affectsShopStatus() {
        return actionType == ModerationActionType.STATUS_CHANGE ||
                actionType == ModerationActionType.BAN ||
                actionType == ModerationActionType.UNBAN ||
                actionType == ModerationActionType.SUSPENSION ||
                actionType == ModerationActionType.SUSPENSION_LIFTED ||
                actionType == ModerationActionType.TEMPORARY_BLOCK ||
                actionType == ModerationActionType.TEMPORARY_BLOCK_REMOVED;
    }

    /**
     * Get severity level based on action type
     */
    public SeverityLevel getDefaultSeverityLevel() {
        switch (actionType) {
            case BAN:
            case COMPLIANCE_VIOLATION:
                return SeverityLevel.CRITICAL;
            case SUSPENSION:
            case TEMPORARY_BLOCK:
            case REJECTION:
                return SeverityLevel.HIGH;
            case WARNING:
            case CONTENT_FLAG:
            case REVERIFICATION_REQUIRED:
                return SeverityLevel.MEDIUM;
            case VERIFICATION:
            case APPROVAL:
            case FEATURE:
            case UNFEATURE:
            case COMPLIANCE_VERIFIED:
                return SeverityLevel.LOW;
            default:
                return SeverityLevel.MEDIUM;
        }
    }

    /**
     * Calculate impact score based on action type and duration
     */
    public void calculateImpactScore() {
        int baseScore = 0;

        switch (actionType) {
            case BAN:
                baseScore = 100;
                break;
            case SUSPENSION:
                baseScore = 80;
                break;
            case TEMPORARY_BLOCK:
                baseScore = 60;
                break;
            case REJECTION:
                baseScore = 70;
                break;
            case WARNING:
                baseScore = 30;
                break;
            case VERIFICATION:
            case APPROVAL:
                baseScore = 10;
                break;
            default:
                baseScore = 50;
        }

        // Adjust for duration
        if (durationDays != null && durationDays > 0) {
            baseScore = Math.min(100, baseScore + (durationDays * 2));
        }

        // Adjust for severity
        if (severityLevel != null) {
            switch (severityLevel) {
                case CRITICAL:
                    baseScore = Math.min(100, baseScore + 20);
                    break;
                case HIGH:
                    baseScore = Math.min(100, baseScore + 10);
                    break;
                case LOW:
                    baseScore = Math.max(0, baseScore - 10);
                    break;
            }
        }

        this.impactScore = baseScore;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();

        // Set default severity level if not provided
        if (this.severityLevel == null) {
            this.severityLevel = getDefaultSeverityLevel();
        }

        // Calculate impact score
        calculateImpactScore();
    }

    public enum ModerationActionType {
        @Schema(description = "Shop status was changed")
        STATUS_CHANGE,
        @Schema(description = "Shop was verified")
        VERIFICATION,
        @Schema(description = "Shop was rejected")
        REJECTION,
        @Schema(description = "Shop was banned")
        BAN,
        @Schema(description = "Shop was unbanned")
        UNBAN,
        @Schema(description = "Shop was featured")
        FEATURE,
        @Schema(description = "Shop was unfeatured")
        UNFEATURE,
        @Schema(description = "Shop was suspended")
        SUSPENSION,
        @Schema(description = "Shop suspension was lifted")
        SUSPENSION_LIFTED,
        @Schema(description = "Shop was reviewed")
        REVIEW,
        @Schema(description = "Shop settings were modified")
        SETTINGS_MODIFICATION,
        @Schema(description = "Shop content was flagged")
        CONTENT_FLAG,
        @Schema(description = "Shop was warned")
        WARNING,
        @Schema(description = "Shop was approved")
        APPROVAL,
        @Schema(description = "Shop was temporarily blocked")
        TEMPORARY_BLOCK,
        @Schema(description = "Shop temporary block was removed")
        TEMPORARY_BLOCK_REMOVED,
        @Schema(description = "Shop was marked for re-verification")
        REVERIFICATION_REQUIRED,
        @Schema(description = "Shop was marked as compliant")
        COMPLIANCE_VERIFIED,
        @Schema(description = "Shop was marked as non-compliant")
        COMPLIANCE_VIOLATION,
        @Schema(description = "Other moderation action")
        OTHER
    }

    // --- Lifecycle methods ---

    public enum SeverityLevel {
        @Schema(description = "Low severity action")
        LOW,
        @Schema(description = "Medium severity action")
        MEDIUM,
        @Schema(description = "High severity action")
        HIGH,
        @Schema(description = "Critical severity action")
        CRITICAL
    }
}
