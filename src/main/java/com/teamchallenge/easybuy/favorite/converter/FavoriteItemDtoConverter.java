package com.teamchallenge.easybuy.favorite.converter;


import com.teamchallenge.easybuy.favorite.dto.FavoriteItemDto;
import com.teamchallenge.easybuy.favorite.entity.FavoriteItemEntity;
import com.teamchallenge.easybuy.product.mapper.GoodsMapper;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = GoodsMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,  injectionStrategy = InjectionStrategy.FIELD)
public interface FavoriteItemDtoConverter {

    @Mapping(target = "productInfo", source = "goods")
    FavoriteItemDto toDto(final FavoriteItemEntity favoriteItemEntity);

}