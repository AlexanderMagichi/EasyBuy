package com.teamchallenge.easybuy.shop.mapper;

import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.shop.entity.ShopContactInfo;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ShopContactInfoMapper {

    // ===================== TO DTO =====================

    @Mapping(source = "id", target = "contactInfoId")
    ShopContactInfoDTO toDto(ShopContactInfo entity);

    // ===================== TO ENTITY =====================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "verificationDate", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShopContactInfo toEntity(ShopContactInfoDTO dto);

    // ===================== UPDATE =====================

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "verified", ignore = true)
    @Mapping(target = "verificationDate", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ShopContactInfoDTO dto,
                             @MappingTarget ShopContactInfo entity);

    // ===================== AFTER MAPPING =====================

    @AfterMapping
    default void normalizeStrings(@MappingTarget ShopContactInfo entity) {

        entity.setContactEmail(normalize(entity.getContactEmail()));
        entity.setContactPhone(normalize(entity.getContactPhone()));
        entity.setSupportEmail(normalize(entity.getSupportEmail()));
        entity.setSupportPhone(normalize(entity.getSupportPhone()));

        entity.setContactPersonName(normalize(entity.getContactPersonName()));
        entity.setContactPersonPosition(normalize(entity.getContactPersonPosition()));

        entity.setBusinessAddress(normalize(entity.getBusinessAddress()));
        entity.setCity(normalize(entity.getCity()));
        entity.setCountry(normalize(entity.getCountry()));
        entity.setPostalCode(normalize(entity.getPostalCode()));

        entity.setWebsiteUrl(normalize(entity.getWebsiteUrl()));

        entity.setFacebookUrl(normalize(entity.getFacebookUrl()));
        entity.setInstagramUrl(normalize(entity.getInstagramUrl()));
        entity.setTelegramUrl(normalize(entity.getTelegramUrl()));
        entity.setViberUrl(normalize(entity.getViberUrl()));

        entity.setWorkingHours(normalize(entity.getWorkingHours()));
        entity.setAdditionalInfo(normalize(entity.getAdditionalInfo()));
    }

    // ===================== UTILS =====================

    default String normalize(String value) {
        if (value == null) return null;

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}