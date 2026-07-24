package com.teamchallenge.easybuy.favorite.converter;


import com.teamchallenge.easybuy.favorite.dto.FavoriteItemDto;
import com.teamchallenge.easybuy.favorite.dto.FavoriteListDto;
import com.teamchallenge.easybuy.favorite.entity.FavoriteItemEntity;
import com.teamchallenge.easybuy.favorite.entity.FavoriteListEntity;
import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import com.teamchallenge.easybuy.product.entity.Goods;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = FavoriteItemDtoConverter.class, unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.FIELD)
public interface FavoriteListDtoConverter {
    FavoriteListDto toDto(final FavoriteListEntity favoriteListEntity);

}