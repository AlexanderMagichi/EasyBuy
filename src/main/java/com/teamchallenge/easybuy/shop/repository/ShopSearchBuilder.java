package com.teamchallenge.easybuy.shop.repository;

import com.teamchallenge.easybuy.shop.dto.ShopSearchParams;
import com.teamchallenge.easybuy.shop.entity.Shop;
import org.springframework.data.jpa.domain.Specification;

public class ShopSearchBuilder {

    private Specification<Shop> spec = Specification.where(null);

    public static ShopSearchBuilder builder() {
        return new ShopSearchBuilder();
    }

    public ShopSearchBuilder withParams(ShopSearchParams params) {
        if (params == null) {
            return this;
        }

        spec = spec
                .and(ShopSpecifications.hasShopId(params.getShopId()))
                .and(ShopSpecifications.likeName(params.getShopName()))
                .and(ShopSpecifications.likeDescription(params.getShopDescription()))
                .and(ShopSpecifications.hasStatus(params.getShopStatus()))
                .and(ShopSpecifications.isFeatured(params.getIsFeatured()))
                .and(ShopSpecifications.hasSellerId(params.getSellerId()))
                .and(ShopSpecifications.hasModeratedByUserId(params.getModeratedByUserId()))
                .and(ShopSpecifications.hasSlug(params.getSlug()))
                .and(ShopSpecifications.createdAtAfterOrEqual(params.getMinCreatedAt()))
                .and(ShopSpecifications.createdAtBeforeOrEqual(params.getMaxCreatedAt()))
                .and(ShopSpecifications.updatedAtAfterOrEqual(params.getMinUpdatedAt()))
                .and(ShopSpecifications.updatedAtBeforeOrEqual(params.getMaxUpdatedAt()))
                .and(ShopSpecifications.hasCategory(params.getSubcategoryId()))
                .and(ShopSpecifications.textSearch(params.getKeyword()));

        return this;
    }

    public Specification<Shop> build() {
        return spec;
    }
}