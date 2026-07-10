package com.teamchallenge.easybuy.cart.endpoint;

import com.teamchallenge.easybuy.cart.api.AddItemsToShoppingCartHelper;
import com.teamchallenge.easybuy.cart.api.ProductQuantityItemUpdater;
import com.teamchallenge.easybuy.cart.api.ShoppingCartItemsDeleter;
import com.teamchallenge.easybuy.cart.api.ShoppingCartProvider;
import com.teamchallenge.easybuy.cart.dto.AddNewItemsToShoppingCartRequest;
import com.teamchallenge.easybuy.cart.dto.DeleteItemsFromShoppingCartRequest;
import com.teamchallenge.easybuy.cart.dto.ShoppingCartDto;
import com.teamchallenge.easybuy.cart.dto.UpdateProductQuantityInShoppingCartItemRequest;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = CartEndpoint.CART_URL)
public class CartEndpoint {

    public static final String CART_URL = "/api/v1/cart";

    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final AddItemsToShoppingCartHelper addItemsToShoppingCartHelper;
    private final ProductQuantityItemUpdater productQuantityItemUpdater;
    private final ShoppingCartProvider shoppingCartProvider;
    private final ShoppingCartItemsDeleter shoppingCartItemsDeleter;

    @PostMapping(value = "/items")
    public ResponseEntity<ShoppingCartDto> addNewItemToShoppingCart(@Valid @RequestBody final AddNewItemsToShoppingCartRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.warn("cart.items.add.invalid: reason=empty_items");
            return ResponseEntity.badRequest().build();
        }
        log.info("cart.items.adding: count={}", request.getItems().size());
        var shoppingCart = addItemsToShoppingCartHelper.add(request.getItems());
        log.info("cart.items.added: cartId={}", shoppingCart.getId());
        return ResponseEntity.ok(shoppingCart);
    }

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart() {
        var userId = securityPrincipalProvider.getUserId();
        log.info("cart.get: userId={}", userId);
        var shoppingCart = shoppingCartProvider.getByUserId(userId);
        log.info("cart.retrieved: userId={}", userId);
        return ResponseEntity.ok(shoppingCart);
    }

    @PatchMapping(value = "/items")
    public ResponseEntity<ShoppingCartDto> updateProductQuantityInShoppingCartItem(@Validated @Valid @RequestBody final UpdateProductQuantityInShoppingCartItemRequest request) {
        var itemId = request.getShoppingCartItemId();
        var quantityChange = request.getProductQuantityChange();
        log.info("cart.items.quantity.updating: itemId={}, change={}", itemId, quantityChange);
        var shoppingCart = productQuantityItemUpdater.update(itemId, quantityChange);
        log.info("cart.items.quantity.updated: itemId={}", itemId);
        return ResponseEntity.ok(shoppingCart);
    }

    @DeleteMapping(value = "/items")
    public ResponseEntity<ShoppingCartDto> deleteItemsFromShoppingCart(@Valid @RequestBody final DeleteItemsFromShoppingCartRequest request) {
        // Validate input to prevent code injection
        if (request.getShoppingCartItemIds() == null || request.getShoppingCartItemIds().isEmpty()) {
            log.warn("cart.items.delete.invalid: reason=empty_ids");
            return ResponseEntity.badRequest().build();
        }

        log.info("cart.items.deleting: count={}", request.getShoppingCartItemIds().size());
        var shoppingCart = shoppingCartItemsDeleter.delete(request);
        log.info("cart.items.deleted");
        return ResponseEntity.ok(shoppingCart);
    }
}
