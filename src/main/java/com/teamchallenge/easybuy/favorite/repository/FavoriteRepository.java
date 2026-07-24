package com.teamchallenge.easybuy.favorite.repository;

import com.teamchallenge.easybuy.favorite.entity.FavoriteListEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteListEntity, UUID> {

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"favoriteItems"})
    Optional<FavoriteListEntity> findByUserId(UUID userId);
}