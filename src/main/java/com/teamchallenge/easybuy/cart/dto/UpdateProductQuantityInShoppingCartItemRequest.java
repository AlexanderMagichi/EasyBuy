package com.teamchallenge.easybuy.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductQuantityInShoppingCartItemRequest {
    private UUID shoppingCartItemId;
    private Integer productQuantityChange;
}
