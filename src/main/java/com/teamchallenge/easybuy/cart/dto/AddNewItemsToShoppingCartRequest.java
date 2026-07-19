package com.teamchallenge.easybuy.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddNewItemsToShoppingCartRequest {
    private Set<NewShoppingCartItemDto> items;
}
