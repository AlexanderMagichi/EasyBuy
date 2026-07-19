package com.teamchallenge.easybuy.cart.dto;

import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartItemDto {
    private UUID id;
    private GoodsDTO productInfo;
    private Integer productQuantity;
}
