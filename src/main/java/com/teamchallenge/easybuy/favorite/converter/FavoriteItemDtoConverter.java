package com.teamchallenge.easybuy.favorite.converter;


import com.teamchallenge.easybuy.favorite.dto.FavoriteItemDto;
import com.teamchallenge.easybuy.favorite.entity.FavoriteItemEntity;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = ProductInfoDtoConverter.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,  injectionStrategy = InjectionStrategy.FIELD)
public interface FavoriteItemDtoConverter {

    @Mapping(target = "productInfo.dateAdded", source = "productInfo.dateAdded", qualifiedByName = "localToOffsetDate")
    FavoriteItemDto toDto(final FavoriteItemEntity favoriteItemEntity);

}