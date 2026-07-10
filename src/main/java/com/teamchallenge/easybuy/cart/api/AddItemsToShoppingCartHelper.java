package com.teamchallenge.easybuy.cart.api;

import com.teamchallenge.easybuy.cart.converter.ShoppingCartDtoConverter;
import com.teamchallenge.easybuy.cart.dto.NewShoppingCartItemDto;
import com.teamchallenge.easybuy.cart.dto.ShoppingCartDto;
import com.teamchallenge.easybuy.cart.entity.ShoppingCart;
import com.teamchallenge.easybuy.cart.entity.ShoppingCartItem;
import com.teamchallenge.easybuy.cart.repository.ShoppingCartRepository;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddItemsToShoppingCartHelper {

    private final ShoppingCartRepository shoppingCartRepository;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final GoodsRepository goodsRepository;
    private final ShoppingCartDtoConverter shoppingCartDtoConverter;
    private final ShoppingCartCreator shoppingCartCreator;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ShoppingCartDto add(final Set<NewShoppingCartItemDto> itemsToAdd) {
        UUID userId = securityPrincipalProvider.getUserId();

        ShoppingCart shoppingCart = shoppingCartCreator.getOrCreate(userId);

        List<ShoppingCartItem> items = createItems(itemsToAdd, shoppingCart);

        ShoppingCart updatedShoppingCart = updateExistingShoppingCart(shoppingCart, items);

        ShoppingCart persistedShoppingCart = shoppingCartRepository.save(updatedShoppingCart);
        return shoppingCartDtoConverter.toDto(persistedShoppingCart);
    }

    private List<ShoppingCartItem> createItems(Set<NewShoppingCartItemDto> itemsToAdd, ShoppingCart shoppingCart) {
        Map<UUID, Integer> productsWithQuantity = itemsToAdd.stream()
                .collect(Collectors.toMap(NewShoppingCartItemDto::getProductId, NewShoppingCartItemDto::getProductQuantity));

        Set<UUID> existingProductIds = shoppingCart.getItems().stream()
                .map(ShoppingCartItem::getGoods)
                .map(Goods::getId)
                .collect(Collectors.toSet());

        Set<UUID> newProductIds = productsWithQuantity.keySet().stream()
                .filter(productId -> !existingProductIds.contains(productId))
                .collect(Collectors.toSet());

        return goodsRepository.findAllById(newProductIds).stream()
                .map(goods ->
                        ShoppingCartItem.builder()
                                .shoppingCart(shoppingCart)
                                .goodsQuantity(productsWithQuantity.get(goods.getId()))
                                .goods(goods)
                                .build()
                )
                .toList();
    }

    private static ShoppingCart updateExistingShoppingCart(ShoppingCart existingShoppingCart,
                                                           List<ShoppingCartItem> shoppingCartItems) {
        int productsQuantity = shoppingCartItems.stream()
                .mapToInt(ShoppingCartItem::getGoodsQuantity)
                .sum();

        existingShoppingCart.setItemsQuantity(existingShoppingCart.getItemsQuantity() + shoppingCartItems.size());
        existingShoppingCart.setGoodsQuantity(existingShoppingCart.getGoodsQuantity() + productsQuantity);
        existingShoppingCart.getItems().addAll(shoppingCartItems);
        return existingShoppingCart;
    }
}
