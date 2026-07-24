package com.teamchallenge.easybuy.favorite.converter;


import com.teamchallenge.easybuy.favorite.dto.FavoriteItemDto;
import com.teamchallenge.easybuy.favorite.dto.FavoriteListDto;
import com.teamchallenge.easybuy.favorite.dto.ListOfFavoriteProductsDto;
import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.FIELD)
public interface ListOfFavoriteProductsDtoConverter {

    @Mapping(target = "products", source = "favoriteItems", qualifiedByName = "toListProductInfoDto")
    ListOfFavoriteProductsDto toListProductDto(FavoriteListDto favoriteList);

    @Named("toListProductInfoDto")
    default List<GoodsDTO> toProductInfoDto(final Set<FavoriteItemDto> favoriteItems) {
        return favoriteItems.stream()
                .map(FavoriteItemDto::productInfo)
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(
                        GoodsDTO::getId,
                        p -> p,
                        (a, b) -> a
                ))
                .values()
                .stream()
                .toList();
    }
}