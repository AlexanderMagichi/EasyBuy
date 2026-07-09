package com.teamchallenge.easybuy.shop.mapper;

import com.teamchallenge.easybuy.shop.dto.ShopSeoSettingsDTO;
import com.teamchallenge.easybuy.shop.entity.ShopSeoSettings;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ShopSeoSettingsMapper {

    ShopSeoSettingsDTO toDto(ShopSeoSettings entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "seoScore", ignore = true)
    @Mapping(target = "lastSeoAudit", ignore = true)
    @Mapping(target = "seoOptimized", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShopSeoSettings toEntity(ShopSeoSettingsDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "seoScore", ignore = true)
    @Mapping(target = "lastSeoAudit", ignore = true)
    @Mapping(target = "seoOptimized", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ShopSeoSettingsDTO dto, @MappingTarget ShopSeoSettings entity);
}

