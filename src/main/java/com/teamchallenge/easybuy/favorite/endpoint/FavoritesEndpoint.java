package com.teamchallenge.easybuy.favorite.endpoint;


import com.teamchallenge.easybuy.favorite.api.FavoriteListProvider;
import com.teamchallenge.easybuy.favorite.api.FavoriteProductAdder;
import com.teamchallenge.easybuy.favorite.api.FavoriteProductDeleter;
import com.teamchallenge.easybuy.favorite.converter.ListOfFavoriteProductsDtoConverter;
import com.teamchallenge.easybuy.favorite.dto.ListOfFavoriteProducts;
import com.teamchallenge.easybuy.favorite.dto.ListOfFavoriteProductsDto;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = FavoritesEndpoint.FAVORITES_URL)
@Tag(name = "Favorite Products", description = "Operations for managing favorite products.")
public class FavoritesEndpoint {

    public static final String FAVORITES_URL = "/api/v1/favorites";

    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final ListOfFavoriteProductsDtoConverter listOfFavoriteProductsDtoConverter;
    private final FavoriteProductAdder favoriteProductAdderHelper;
    private final FavoriteListProvider favoriteListProvider;
    private final FavoriteProductDeleter favoriteProductDeleter;

    @PostMapping
    @Operation(summary = "Add products to favorites")
    public ResponseEntity<ListOfFavoriteProductsDto> addListOfFavoriteProducts(@Validated @Valid @RequestBody final ListOfFavoriteProducts request) {
        log.info("favourites.adding: count={}", request.getGoods().size());
        var userId = securityPrincipalProvider.getUserId();
        var favoriteList = favoriteProductAdderHelper.add(request, userId);
        var response = listOfFavoriteProductsDtoConverter.toListProductDto(favoriteList);
        log.info("favourites.added: count={}, userId={}", request.getGoods().size(), userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Retrieve all favorite products")
    public ResponseEntity<ListOfFavoriteProductsDto> getListOfFavoriteProducts() {
        var userId = securityPrincipalProvider.getUserId();
        log.info("favourites.fetching: userId={}", userId);
        var favoriteList = favoriteListProvider.getFavoriteListDto(userId);
        var response = listOfFavoriteProductsDtoConverter.toListProductDto(favoriteList);
        log.info("favourites.retrieved: count={}, userId={}", response.getProducts().size(), userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{productId}")
    @Operation(summary = "Remove a product by ID from favorite product list.")
    public ResponseEntity<Void> removeProductFromFavorite(@PathVariable final UUID productId) {
        // Validate UUID input to prevent code injection
        if (productId == null) {
            log.warn("favourites.remove.invalid: reason=null_productId");
            return ResponseEntity.badRequest().build();
        }
        
        log.info("favourites.removing: productId={}", productId);
        var userId = securityPrincipalProvider.getUserId();
        favoriteProductDeleter.delete(productId, userId);
        log.info("favourites.removed: productId={}, userId={}", productId, userId);
        return ResponseEntity.ok().build();
    }
}
