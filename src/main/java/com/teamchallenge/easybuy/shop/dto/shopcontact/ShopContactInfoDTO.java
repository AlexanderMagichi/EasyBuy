package com.teamchallenge.easybuy.shop.dto.shopcontact;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.teamchallenge.easybuy.infrastructure.enums.ContactMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.Instant;
import java.util.UUID;

import static com.teamchallenge.easybuy.infrastructure.util.StringUtils.hasText;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data transfer object for shop contact information")
public class ShopContactInfoDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID contactInfoId;

    // === REQUIRED ===

    @NotBlank
    @Email
    @Size(max = 255)
    private String contactEmail;

    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @Size(max = 20)
    private String contactPhone;

    @Size(max = 100)
    private String contactPersonName;

    // === OPTIONAL CONTACT ===

    @Email
    @Size(max = 255)
    private String supportEmail;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    @Size(max = 20)
    private String supportPhone;

    @Size(max = 100)
    private String contactPersonPosition;

    // === ADDRESS ===

    @Size(max = 255)
    private String businessAddress;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    @Size(max = 20)
    private String postalCode;

    // === ONLINE ===

    @URL
    @Size(max = 500)
    private String websiteUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    private String facebookUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    private String instagramUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    private String telegramUrl;

    @Pattern(regexp = "^(https?://)?[\\w.-]+\\.[a-z]{2,}.*$")
    @Size(max = 500)
    private String viberUrl;

    // === EXTRA ===

    @Size(max = 1000)
    private String workingHours;

    @Size(max = 1000)
    private String additionalInfo;

    // === SETTINGS ===

    private ContactMethod preferredContactMethod;

    // === READ ONLY ===

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isVerified;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant verificationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean isActive;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;

    // === COMPUTED ===

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Boolean getIsComplete() {
        return hasText(contactEmail) &&
                hasText(contactPhone) &&
                hasText(businessAddress);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Boolean getHasSocialMedia() {
        return hasText(facebookUrl) ||
                hasText(instagramUrl) ||
                hasText(telegramUrl) ||
                hasText(viberUrl);
    }

    // === VALIDATION GROUPS ===

    public interface CreateValidation {}
    public interface UpdateValidation {}
    public interface BasicValidation {}

    // === NESTED DTOs ===

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicContactDTO {

        @NotBlank(groups = CreateValidation.class)
        @Email
        private String contactEmail;

        @NotBlank(groups = CreateValidation.class)
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
        private String contactPhone;

        private String contactPersonName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialMediaDTO {
        private String facebookUrl;
        private String instagramUrl;
        private String telegramUrl;
        private String viberUrl;
    }
}