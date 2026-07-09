package com.teamchallenge.easybuy.shop.mapper;

import com.teamchallenge.easybuy.shop.dto.shopanalytics.ShopAnalyticsDTO;
import com.teamchallenge.easybuy.shop.entity.ShopAnalytics;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface ShopAnalyticsMapper {

    ShopAnalyticsDTO toDto(ShopAnalytics entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "averageOrderValue", ignore = true)
    @Mapping(target = "conversionRate", ignore = true)
    @Mapping(target = "cancellationRate", ignore = true)
    @Mapping(target = "returnRate", ignore = true)
    @Mapping(target = "healthScore", ignore = true)
    @Mapping(target = "inactiveDays", ignore = true)
    @Mapping(target = "deadShop", ignore = true)
    @Mapping(target = "lastCalculatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ShopAnalytics toEntity(ShopAnalyticsDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "averageOrderValue", ignore = true)
    @Mapping(target = "conversionRate", ignore = true)
    @Mapping(target = "cancellationRate", ignore = true)
    @Mapping(target = "returnRate", ignore = true)
    @Mapping(target = "healthScore", ignore = true)
    @Mapping(target = "inactiveDays", ignore = true)
    @Mapping(target = "deadShop", ignore = true)
    @Mapping(target = "lastCalculatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ShopAnalyticsDTO dto, @MappingTarget ShopAnalytics entity);
}

