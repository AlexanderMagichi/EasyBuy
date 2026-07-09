package com.teamchallenge.easybuy.shop.mapper;

import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import com.teamchallenge.easybuy.shop.entity.ShopTaxInfo;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShopTaxMapper {

    ShopTaxInfoDTO toDto(ShopTaxInfo entity);

    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "id", ignore = true)
    ShopTaxInfo toEntity(ShopTaxInfoDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ShopTaxInfoDTO dto, @MappingTarget ShopTaxInfo entity);
}