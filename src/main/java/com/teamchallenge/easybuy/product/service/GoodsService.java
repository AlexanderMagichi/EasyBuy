package com.teamchallenge.easybuy.product.service;

import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import com.teamchallenge.easybuy.user.entity.Authority;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.product.exception.GoodsNotFoundException;
import com.teamchallenge.easybuy.product.mapper.GoodsMapper;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.entity.category.Category;
import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.product.repository.GoodsSpecifications;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final GoodsMapper goodsMapper;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Autowired
    public GoodsService(
            GoodsRepository goodsRepository,
            GoodsMapper goodsMapper,
            ShopRepository shopRepository,
            UserRepository userRepository
    ) {
        this.goodsRepository = goodsRepository;
        this.goodsMapper = goodsMapper;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

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
        requireCanManageShop(goodsDTO.getShopId());

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
        requireCanManageShop(existingShopId);

        // Non-admin users cannot move goods between shops.
        if (goodsDTO.getShopId() != null && !goodsDTO.getShopId().equals(existingShopId)) {
            UserEntity currentUser = getCurrentUserOrThrow();
            if (!hasAuthority(currentUser, Authority.SUPER_ADMIN)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot move goods to another shop");
            }
        }

        if (!existingGoods.getArt().equals(goodsDTO.getArt()) && goodsRepository.existsByArt(goodsDTO.getArt())) {
            throw new IllegalArgumentException("Goods with art " + goodsDTO.getArt() + " already exists");
        }
        Goods updatedGoods = goodsMapper.toEntity(goodsDTO);
        updatedGoods.setId(id); // Preserve ID
        return goodsMapper.toDto(goodsRepository.save(updatedGoods));
    }

    @Transactional
    @CacheEvict(value = "goods", allEntries = true)
    public void deleteGoods(UUID id) {
        Goods goods = goodsRepository.findById(id)
                .orElseThrow(() -> new GoodsNotFoundException(id));

        UUID shopId = goods.getShop() != null ? goods.getShop().getShopId() : null;
        requireCanManageShop(shopId);

        goodsRepository.deleteById(id);
    }

    private void requireCanManageShop(UUID shopId) {
        if (shopId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop ID is required");
        }

        UserEntity currentUser = getCurrentUserOrThrow();
        Set<Authority> authorities = extractAuthorities(currentUser);

        if (authorities.contains(Authority.SUPER_ADMIN)) {
            return;
        }

        if (authorities.contains(Authority.SELLER)) {
            boolean ownsShop = shopRepository.existsByShopIdAndSeller_Id(shopId, currentUser.getId());
            if (!ownsShop) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seller can manage only own shop goods");
            }
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions to manage goods");
    }

    private UserEntity getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user is not found"));
    }

    private boolean hasAuthority(UserEntity user, Authority authority) {
        return extractAuthorities(user).contains(authority);
    }

    private Set<Authority> extractAuthorities(UserEntity user) {
        return user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .map(Authority::valueOf)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsSearch", key = "{#id, #art, #name, #price, #stock, #reviewsCount, #shopId, #category?.id, #goodsStatus, #discountStatus, #rating}")
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