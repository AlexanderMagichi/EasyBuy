package com.teamchallenge.easybuy.cart.converter;

import com.teamchallenge.easybuy.cart.dto.ShoppingCartItemDto;
import com.teamchallenge.easybuy.cart.entity.ShoppingCartItem;
import com.teamchallenge.easybuy.product.mapper.GoodsMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GoodsMapper.class)
public interface ShoppingCartItemDtoConverter {

    @Named("toShoppingCartItemDto")
    @Mapping(target = "productInfo", source = "goods")
    @Mapping(target = "productQuantity", source = "goodsQuantity")
    ShoppingCartItemDto toDto(final ShoppingCartItem entity);
}
