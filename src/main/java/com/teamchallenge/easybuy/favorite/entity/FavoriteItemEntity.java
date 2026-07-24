package com.teamchallenge.easybuy.favorite.entity;


import com.teamchallenge.easybuy.product.entity.Goods;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favorite_item")
public class FavoriteItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "favorite_id", nullable = false, referencedColumnName = "id")
    private FavoriteListEntity favoriteListEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id", nullable = false)
    private Goods goods;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteItemEntity that = (FavoriteItemEntity) o;
        return Objects.equals(favoriteListEntity, that.favoriteListEntity) && Objects.equals(goods, that.goods);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(favoriteListEntity)
                .append(goods)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "FavoriteItem{" +
                "id=" + id +
                ", goods=" + goods +
                '}';
    }
}