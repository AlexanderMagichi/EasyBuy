package com.teamchallenge.easybuy.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartDto {
    private UUID id;
    private UUID userId;
    private List<ShoppingCartItemDto> items;
    private BigDecimal itemsTotalPrice;
}
