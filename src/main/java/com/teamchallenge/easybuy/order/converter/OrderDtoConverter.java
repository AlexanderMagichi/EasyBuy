package com.teamchallenge.easybuy.order.converter;

import com.teamchallenge.easybuy.cart.dto.ShoppingCartItemDto;
import com.teamchallenge.easybuy.openapi.dto.OrderDto;
import com.teamchallenge.easybuy.order.entity.Order;
import com.teamchallenge.easybuy.order.entity.OrderItem;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface OrderDtoConverter {

    OrderDto toResponseDto(final Order orderEntity);

    @Named("toOrderItemDto")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "productInfo.id")
    @Mapping(target = "productName", source = "productInfo.name")
    @Mapping(target = "productPrice", source = "productInfo.price")
    @Mapping(target = "productsQuantity", source = "productQuantity")
    OrderItem toOrderItem(ShoppingCartItemDto cartItem);
}