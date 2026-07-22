package com.teamchallenge.easybuy.favorite.dto;

import com.zufar.icedlatte.openapi.dto.ProductInfoDto;

import java.util.UUID;

public record FavoriteItemDto(UUID id,
                              ProductInfoDto productInfo) {}