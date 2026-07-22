package com.teamchallenge.easybuy.favorite.converter;


import com.teamchallenge.easybuy.favorite.dto.FavoriteItemDto;
import com.teamchallenge.easybuy.favorite.dto.FavoriteListDto;
import com.teamchallenge.easybuy.favorite.entity.FavoriteItemEntity;
import com.teamchallenge.easybuy.favorite.entity.FavoriteListEntity;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = FavoriteItemDtoConverter.class, unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.FIELD)
public interface FavoriteListDtoConverter {
    @Mapping(target = "favoriteItems", source = "favoriteItems", qualifiedByName = "mapFavoriteItems")
    FavoriteListDto toDto(final FavoriteListEntity favoriteListEntity);

    @Mapping(target = "dateAdded", source = "dateAdded", qualifiedByName = "localToOffsetDate")
    ProductInfoDto convertProductInfoDto(ProductInfo productInfo);

    @Named("mapFavoriteItems")
    default FavoriteItemDto toFavoriteItemDto(FavoriteItemEntity itemEntity) {
        UUID favoriteItemEntityId = itemEntity.getId();
        ProductInfo productInfo = itemEntity.getProductInfo();

        ProductInfoDto productInfoDto = convertProductInfoDto(productInfo);

        return new FavoriteItemDto(favoriteItemEntityId, productInfoDto);
    }

    @Named("localToOffsetDate")
    default OffsetDateTime offsetToLocalDate(LocalDateTime value) {
        if (value != null) {
            return OffsetDateTime.of(value, ZoneOffset.UTC);
        }
        return null;
    }

}