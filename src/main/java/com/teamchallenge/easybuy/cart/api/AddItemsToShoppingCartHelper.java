package com.teamchallenge.easybuy.cart.api;

import com.teamchallenge.easybuy.cart.converter.ShoppingCartDtoConverter;
import com.teamchallenge.easybuy.cart.entity.ShoppingCart;
import com.teamchallenge.easybuy.cart.entity.ShoppingCartItem;
import com.teamchallenge.easybuy.cart.repository.ShoppingCartRepository;
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
    private final ProductInfoRepository productInfoRepository;
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
                .map(ShoppingCartItem::getProductInfo)
                .map(ProductInfo::getId)
                .collect(Collectors.toSet());

        Set<UUID> newProductIds = productsWithQuantity.keySet().stream()
                .filter(productId -> !existingProductIds.contains(productId))
                .collect(Collectors.toSet());

        return productInfoRepository.findAllById(newProductIds).stream()
                .map(productInfo ->
                        ShoppingCartItem.builder()
                                .shoppingCart(shoppingCart)
                                .productQuantity(productsWithQuantity.get(productInfo.getId()))
                                .productInfo(productInfo)
                                .build()
                )
                .toList();
    }

    private static ShoppingCart updateExistingShoppingCart(ShoppingCart existingShoppingCart,
                                                           List<ShoppingCartItem> shoppingCartItems) {
        int productsQuantity = shoppingCartItems.stream()
                .mapToInt(ShoppingCartItem::getProductQuantity)
                .sum();

        existingShoppingCart.setItemsQuantity(existingShoppingCart.getItemsQuantity() + shoppingCartItems.size());
        existingShoppingCart.setProductsQuantity(existingShoppingCart.getProductsQuantity() + productsQuantity);
        existingShoppingCart.getItems().addAll(shoppingCartItems);
        return existingShoppingCart;
    }
}
