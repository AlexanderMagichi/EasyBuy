package com.teamchallenge.easybuy.shop.mapper;

import com.teamchallenge.easybuy.shop.dto.ShopDTO;
import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                ShopContactInfoMapper.class,
                ShopBillingMapper.class,
                ShopTaxMapper.class,
                ShopSeoSettingsMapper.class
        },
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ShopMapper {

    // ===== ENTITY → DTO =====

    @Mapping(source = "seller.id", target = "sellerId")
    @Mapping(source = "moderatedByUser.id", target = "moderatedByUserId")
    @Mapping(source = "shopContactInfo", target = "shopContactInfo")
    @Mapping(source = "shopBillingInfo", target = "shopBillingInfo")
    @Mapping(source = "shopTaxInfo", target = "shopTaxInfo")
    @Mapping(source = "seoSettings", target = "seoSettings")
    ShopDTO toDto(Shop shop);


    // ===== DTO → ENTITY =====

    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "moderatedByUser", ignore = true)
    @Mapping(target = "shopContactInfo", ignore = true)
    @Mapping(target = "shopBillingInfo", ignore = true)
    @Mapping(target = "shopTaxInfo", ignore = true)
    @Mapping(target = "seoSettings", ignore = true)
    Shop toEntity(ShopDTO dto);


    // ===== PARTIAL UPDATE =====

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "shopId", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "moderatedByUser", ignore = true)
    @Mapping(target = "shopContactInfo", ignore = true)
    @Mapping(target = "shopBillingInfo", ignore = true)
    @Mapping(target = "shopTaxInfo", ignore = true)
    @Mapping(target = "seoSettings", ignore = true)
    void updateEntityFromDto(ShopDTO dto, @MappingTarget Shop entity);


    // ===== POST PROCESSING =====

    @AfterMapping
    default void afterMappingToEntity(ShopDTO dto, @MappingTarget Shop entity) {

        if (entity.getSlug() == null && entity.getShopName() != null) {
            entity.setSlug(
                    entity.getShopName()
                            .trim()
                            .toLowerCase()
                            .replaceAll("[^a-z0-9-]", "-")
                            .replaceAll("-+", "-")
                            .replaceAll("^-|-$", "")
            );
        }

        if (entity.getShopStatus() == null) {
            entity.setShopStatus(Shop.ShopStatus.PENDING);
        }

        if (entity.getShopType() == null) {
            entity.setShopType(Shop.ShopType.RETAILER);
        }
    }
}