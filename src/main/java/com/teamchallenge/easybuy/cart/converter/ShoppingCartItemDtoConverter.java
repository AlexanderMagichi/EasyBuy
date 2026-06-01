package com.teamchallenge.easybuy.cart.converter;

import com.teamchallenge.easybuy.cart.entity.ShoppingCartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ProductInfoDtoConverter.class)
public interface ShoppingCartItemDtoConverter {

    @Named("toShoppingCartItemDto")
    @Mapping(target = "productInfo", source = "productInfo", qualifiedByName = {"toProductInfoDto"})
    ShoppingCartItemDto toDto(final ShoppingCartItem entity);
}
