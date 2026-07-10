package com.teamchallenge.easybuy.cart.api;

import com.teamchallenge.easybuy.cart.entity.ShoppingCartItem;
import org.mapstruct.Named;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Set;

@Service
public class ItemsTotalPriceCalculator {

    @Named("toItemsTotalPrice")
    public BigDecimal calculate(Set<ShoppingCartItem> items) {
        return items.stream()
                .map(item -> item.getGoods().getPrice().multiply(BigDecimal.valueOf(item.getGoodsQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
