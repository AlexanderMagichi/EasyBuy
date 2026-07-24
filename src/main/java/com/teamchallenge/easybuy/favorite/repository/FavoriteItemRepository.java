package com.teamchallenge.easybuy.favorite.repository;

import com.teamchallenge.easybuy.favorite.entity.FavoriteItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItemEntity, UUID> {
}
