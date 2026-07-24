package com.teamchallenge.easybuy.favorite.dto;
import com.teamchallenge.easybuy.product.dto.GoodsDTO;

import java.util.UUID;

public record FavoriteItemDto(UUID id,
                              GoodsDTO productInfo) {}