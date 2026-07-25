package com.teamchallenge.easybuy.favorite.dto;

import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListOfFavoriteProductsDto {
    private List<GoodsDTO> products;
}
