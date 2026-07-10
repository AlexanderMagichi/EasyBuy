package com.teamchallenge.easybuy.product.service;

import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.entity.category.Category;
import com.teamchallenge.easybuy.product.exception.GoodsNotFoundException;
import com.teamchallenge.easybuy.product.mapper.GoodsMapper;
import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.product.repository.GoodsSpecifications;
import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing goods. Ownership is validated via ShopAccessGuard
 * using the StoreMembership architecture.
 */
@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final GoodsMapper goodsMapper;
    private final ShopAccessGuard accessGuard;

    @Transactional(readOnly = true)
    @Cacheable(value = "goods", key = "'all'")
    public List<GoodsDTO> getAllGoods() {
        return goodsRepository.findAll().stream()
                .map(goodsMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goods", key = "#id")
    public GoodsDTO getGoodsById(UUID id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));
        return goodsMapper.toDto(goods);
    }

    @Transactional
    @CacheEvict(value = "goods", allEntries = true)
    public GoodsDTO createGoods(GoodsDTO goodsDTO) {
        accessGuard.requireCanManageShop(goodsDTO.getShopId());

        if (goodsRepository.existsByArt(goodsDTO.getArt())) {
            throw new IllegalArgumentException("Goods with art " + goodsDTO.getArt() + " already exists");
        }

        Goods goods = goodsMapper.toEntity(goodsDTO);
        return goodsMapper.toDto(goodsRepository.save(goods));
    }

    @Transactional
    @CacheEvict(value = "goods", allEntries = true)
    public GoodsDTO updateGoods(UUID id, GoodsDTO goodsDTO) {
        Goods existingGoods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));

        UUID existingShopId = existingGoods.getShop() != null ? existingGoods.getShop().getShopId() : null;

        accessGuard.requireCanManageShop(existingShopId);

        if (goodsDTO.getShopId() != null && !goodsDTO.getShopId().equals(existingShopId)) {
            if (!accessGuard.isCurrentUserAdmin()) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot move goods to another shop");
            }
        }

        if (!existingGoods.getArt().equals(goodsDTO.getArt()) && goodsRepository.existsByArt(goodsDTO.getArt())) {
            throw new IllegalArgumentException("Goods with art " + goodsDTO.getArt() + " already exists");
        }

        Goods updatedGoods = goodsMapper.toEntity(goodsDTO);
        updatedGoods.setId(id);
        return goodsMapper.toDto(goodsRepository.save(updatedGoods));
    }

    @Transactional
    @CacheEvict(value = "goods", allEntries = true)
    public void deleteGoods(UUID id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));

        UUID shopId = goods.getShop() != null ? goods.getShop().getShopId() : null;
        accessGuard.requireCanManageShop(shopId);

        goodsRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GoodsDTO> searchGoods(UUID id, String art, String name, BigDecimal price, Integer stock,
                                      Integer reviewsCount, UUID shopId, Category category,
                                      Goods.GoodsStatus goodsStatus, Goods.DiscountStatus discountStatus, Integer rating) {

        Specification<Goods> spec = Specification.where(GoodsSpecifications.hasId(id))
                .and(GoodsSpecifications.hasArt(art))
                .and(GoodsSpecifications.hasName(name))
                .and(GoodsSpecifications.hasPrice(price))
                .and(GoodsSpecifications.hasStock(stock))
                .and(GoodsSpecifications.hasReviewsCount(reviewsCount))
                .and(GoodsSpecifications.hasShopId(shopId))
                .and(GoodsSpecifications.hasCategory(category))
                .and(GoodsSpecifications.hasGoodsStatus(goodsStatus))
                .and(GoodsSpecifications.hasDiscountStatus(discountStatus))
                .and(GoodsSpecifications.hasRating(rating));

        return goodsRepository.findAll(spec).stream()
                .map(goodsMapper::toDto)
                .toList();
    }
}