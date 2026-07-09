package com.teamchallenge.easybuy.product.mapper;

import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.shop.entity.Shop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring",
        uses = {GoodsImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoodsMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.shopId", target = "shopId")
    GoodsDTO toDto(Goods goods);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "additionalImages", ignore = true)
    @Mapping(source = "shopId", target = "shop", qualifiedByName = "uuidToShop")
    Goods toEntity(GoodsDTO goodsDTO);

    @Named("uuidToShop")
    default Shop uuidToShop(UUID id) {
        if (id == null) return null;
        Shop shop = new Shop();
        shop.setShopId(id);
        return shop;
    }
}