package com.teamchallenge.easybuy.shop.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.URL;

import com.teamchallenge.easybuy.common.enums.ContactMethod;

import java.time.Instant;
import java.util.UUID;

import static com.teamchallenge.easybuy.common.util.StringUtils.hasText;

@Entity
@Table(name = "shop_contact_info", indexes = {
        @Index(name = "idx_contact_info_shop_id", columnList = "shop_id"),
        @Index(name = "idx_contact_info_email", columnList = "contact_email"),
        @Index(name = "idx_contact_info_phone", columnList = "contact_phone"),
        @Index(name = "idx_contact_info_location", columnList = "city,country"),
        @Index(name = "idx_contact_info_status", columnList = "is_active,is_verified")
})
/**
 * Represents the ShopContactInfo persistence entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
@Schema(description = "Contains contact information of a shop.")
public class ShopContactInfo {

    @Id
    @Column(name = "shop_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "shop_id")
    @ToString.Exclude
    private Shop shop;

    // --- CONTACT ---

    @Email
    @Size(max = 255)
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @Size(max = 20)
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Email
    @Size(max = 255)
    @Column(name = "support_email", length = 255)
    private String supportEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @Size(max = 20)
    @Column(name = "support_phone", length = 20)
    private String supportPhone;

    @URL
    @Size(max = 500)
    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Pattern(regexp = "^(https?://)?[\\w./%-]+\\.(jpg|jpeg|png|webp|svg)$")
    @Size(max = 500)
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    // --- PERSON ---

    @Size(max = 100)
    @Column(name = "contact_person_name", length = 100)
    private String contactPersonName;

    @Size(max = 100)
    @Column(name = "contact_person_position", length = 100)
    private String contactPersonPosition;

    // --- ADDRESS ---

    @Size(max = 255)
    @Column(name = "business_address", length = 255)
    private String businessAddress;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    // --- SOCIAL ---

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    @Column(name = "facebook_url", length = 500)
    private String facebookUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    @Column(name = "telegram_url", length = 500)
    private String telegramUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    @Column(name = "viber_url", length = 500)
    private String viberUrl;

    // --- EXTRA ---

    @Size(max = 1000)
    @Column(name = "working_hours", columnDefinition = "TEXT")
    private String workingHours;

    @Size(max = 1000)
    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Column(name = "verification_date")
    private Instant verificationDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_contact_method")
    private ContactMethod preferredContactMethod;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // --- BUSINESS METHODS ---

    public boolean isComplete() {
        return hasText(contactEmail) &&
                hasText(contactPhone) &&
                hasText(businessAddress);
    }

    public String getPrimaryEmail() {
        return hasText(contactEmail) ? contactEmail : supportEmail;
    }

    public String getPrimaryPhone() {
        return hasText(contactPhone) ? contactPhone : supportPhone;
    }

    public String getFullAddress() {
        StringBuilder address = new StringBuilder();

        if (hasText(businessAddress)) {
            address.append(businessAddress);
        }
        if (hasText(city)) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        if (hasText(postalCode)) {
            if (address.length() > 0) address.append(", ");
            address.append(postalCode);
        }
        if (hasText(country)) {
            if (address.length() > 0) address.append(", ");
            address.append(country);
        }

        return address.toString();
    }

    public boolean hasSocialMedia() {
        return hasText(facebookUrl) ||
                hasText(instagramUrl) ||
                hasText(telegramUrl) ||
                hasText(viberUrl);
    }

    public String getContactPersonInfo() {
        if (!hasText(contactPersonName)) {
            return null;
        }

        StringBuilder info = new StringBuilder(contactPersonName);

        if (hasText(contactPersonPosition)) {
            info.append(" (").append(contactPersonPosition).append(")");
        }

        return info.toString();
    }

    public void markVerified() {
        this.verified = true;
        this.verificationDate = Instant.now();
    }

    // --- LIFECYCLE ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
