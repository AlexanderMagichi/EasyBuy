package com.teamchallenge.easybuy.favorite.api;


import com.teamchallenge.easybuy.favorite.converter.FavoriteListDtoConverter;
import com.teamchallenge.easybuy.favorite.dto.FavoriteListDto;
import com.teamchallenge.easybuy.favorite.entity.FavoriteItemEntity;
import com.teamchallenge.easybuy.favorite.entity.FavoriteListEntity;
import com.teamchallenge.easybuy.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteProductAdder {

    private final FavoriteRepository favoriteRepository;
    //todo: Change this to ProductInfoRepository when it is implemented
    private final ProductInfoRepository productInfoRepository;
    private final FavoriteListDtoConverter favoriteListDtoConverter;
    private final FavoriteListProvider favoriteListProvider;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public FavoriteListDto add(final ListOfFavoriteProducts listOfFavoriteProducts, final UUID userId) {
        FavoriteListEntity favoriteListEntity = favoriteListProvider.getFavoriteListEntity(userId);

        Set<UUID> existingFavoriteProductIds = extractFavoriteProductIds(favoriteListEntity);
        Set<UUID> newFavoriteItemIds = filterNewFavoriteProductIds(listOfFavoriteProducts, existingFavoriteProductIds);

        Set<FavoriteItemEntity> newFavoriteItems = createFavoriteItems(newFavoriteItemIds, favoriteListEntity);
        favoriteListEntity.getFavoriteItems().addAll(newFavoriteItems);

        FavoriteListEntity updatedFavoriteListEntity = favoriteRepository.save(favoriteListEntity);
        return favoriteListDtoConverter.toDto(updatedFavoriteListEntity);
    }

    private Set<UUID> extractFavoriteProductIds(FavoriteListEntity favoriteListEntity) {
        return favoriteListEntity.getFavoriteItems().stream()
                .map(item -> item.getProductInfo().getId())
                .collect(Collectors.toSet());
    }

    private Set<UUID> filterNewFavoriteProductIds(ListOfFavoriteProducts listOfFavoriteProducts, Set<UUID> existingIds) {
        return listOfFavoriteProducts.getProductIds().stream()
                .filter(productId -> !existingIds.contains(productId))
                .collect(Collectors.toSet());
    }

    private Set<FavoriteItemEntity> createFavoriteItems(Set<UUID> productIds, FavoriteListEntity favoriteListEntity) {
        return productInfoRepository.findAllById(productIds).stream()
                .map(productInfo -> FavoriteItemEntity.builder()
                        .favoriteListEntity(favoriteListEntity)
                        .productInfo(productInfo)
                        .build())
                .collect(Collectors.toSet());
    }
}
