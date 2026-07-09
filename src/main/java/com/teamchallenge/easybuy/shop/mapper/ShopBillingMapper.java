package com.teamchallenge.easybuy.shop.mapper;

import com.teamchallenge.easybuy.shop.dto.shopbillinginfo.ShopBillingInfoDTO;
import com.teamchallenge.easybuy.shop.entity.ShopBillingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShopBillingMapper {

    ShopBillingInfoDTO toDto(ShopBillingInfo entity);

    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stripeAccountId", ignore = true)
    ShopBillingInfo toEntity(ShopBillingInfoDTO dto);
}