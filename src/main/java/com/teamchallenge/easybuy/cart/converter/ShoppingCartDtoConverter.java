package com.teamchallenge.easybuy.cart.converter;

import com.teamchallenge.easybuy.cart.api.ItemsTotalPriceCalculator;
import com.teamchallenge.easybuy.cart.dto.ShoppingCartDto;
import com.teamchallenge.easybuy.cart.entity.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ShoppingCartItemDtoConverter.class, ItemsTotalPriceCalculator.class})
public interface ShoppingCartDtoConverter {

    @Mapping(target = "userId", source = "entity.userId")
    @Mapping(target = "items", source = "entity.items", qualifiedByName = {"toShoppingCartItemDto"})
    @Mapping(target = "itemsTotalPrice", source = "entity.items", qualifiedByName = {"toItemsTotalPrice"})
    ShoppingCartDto toDto(final ShoppingCart entity);
}