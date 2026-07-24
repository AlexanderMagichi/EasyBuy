package com.teamchallenge.easybuy.favorite.api;


import com.teamchallenge.easybuy.favorite.converter.FavoriteListDtoConverter;
import com.teamchallenge.easybuy.favorite.dto.FavoriteListDto;
import com.teamchallenge.easybuy.favorite.entity.FavoriteListEntity;
import com.teamchallenge.easybuy.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteListProvider {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteListDtoConverter favoriteListDtoConverter;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public FavoriteListEntity getFavoriteListEntity(final UUID userId) {
        return favoriteRepository.findByUserId(userId)
                .orElseGet(() -> favoriteRepository.save(createNewFavoriteList(userId)));
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public FavoriteListDto getFavoriteListDto(final UUID userId) {
        return favoriteListDtoConverter.toDto(getFavoriteListEntity(userId));
    }

    private FavoriteListEntity createNewFavoriteList(UUID userId) {
        return FavoriteListEntity.builder()
                .userId(userId)
                .favoriteItems(new HashSet<>())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
