package com.teamchallenge.easybuy.product.service;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.product.dto.GoodsDTO;
import com.teamchallenge.easybuy.product.exception.GoodsNotFoundException;
import com.teamchallenge.easybuy.product.mapper.GoodsMapper;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.entity.category.Category;
import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import com.teamchallenge.easybuy.user.entity.Authority;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.entity.UserGrantedAuthority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoodsServiceTest {

    @Mock
    private GoodsRepository goodsRepository;

    @Mock
    private GoodsMapper goodsMapper;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoodsService goodsService;

    private Goods goods;
    private GoodsDTO goodsDTO;
    private UUID goodsId;
    private UUID currentUserId;
    private String currentUserEmail;
    private UserEntity currentSeller;

    @BeforeEach
    void setUp() {
        goodsId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID shopId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();
        currentUserEmail = "seller@example.com";

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        Shop shop = new Shop();
        shop.setShopId(shopId);

        goods = new Goods();
        goods.setId(goodsId);
        goods.setArt("ART-001");
        goods.setName("Wireless Mouse");
        goods.setDescription("A high-precision wireless mouse.");
        goods.setPrice(new BigDecimal("1499.99"));
        goods.setMainImageUrl("https://example.com/images/mouse.jpg");
        goods.setStock(120);
        goods.setReviewsCount(45);
        goods.setShop(shop);
        goods.setCategory(category);
        goods.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        goods.setDiscountStatus(Goods.DiscountStatus.NONE);
        goods.setDiscountValue(null);
        goods.setRating(4);
        goods.setSlug("wireless-mouse-123");
        goods.setMetaTitle("Wireless Mouse - Best Price");
        goods.setMetaDescription("High-quality wireless mouse at the best price.");
        goods.setCreatedAt(Instant.now());
        goods.setUpdatedAt(Instant.now());
        goods.setAdditionalImages(new ArrayList<>());

        goodsDTO = new GoodsDTO();
        goodsDTO.setId(goodsId);
        goodsDTO.setArt("ART-001");
        goodsDTO.setName("Wireless Mouse");
        goodsDTO.setDescription("A high-precision wireless mouse.");
        goodsDTO.setPrice(new BigDecimal("1499.99"));
        goodsDTO.setMainImageUrl("https://example.com/images/mouse.jpg");
        goodsDTO.setStock(120);
        goodsDTO.setReviewsCount(45);
        goodsDTO.setShopId(shopId);
        goodsDTO.setCategoryId(categoryId);
        goodsDTO.setGoodsStatus(Goods.GoodsStatus.ACTIVE);
        goodsDTO.setDiscountStatus(Goods.DiscountStatus.NONE);
        goodsDTO.setDiscountValue(null);
        goodsDTO.setRating(4);
        goodsDTO.setSlug("wireless-mouse-123");
        goodsDTO.setMetaTitle("Wireless Mouse - Best Price");
        goodsDTO.setMetaDescription("High-quality wireless mouse at the best price.");
        goodsDTO.setCreatedAt(goods.getCreatedAt());
        goodsDTO.setUpdatedAt(goods.getUpdatedAt());
        goodsDTO.setAdditionalImages(new ArrayList<>());

        currentSeller = new UserEntity();
        currentSeller.setId(currentUserId);
        currentSeller.setEmail(currentUserEmail);
        currentSeller.addAuthority(UserGrantedAuthority.builder().authority(Authority.SELLER).build());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserEmail, "password", Collections.emptyList())
        );
        when(userRepository.findByEmail(currentUserEmail)).thenReturn(Optional.of(currentSeller));
        when(shopRepository.existsByShopIdAndSeller_Id(any(UUID.class), eq(currentUserId))).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("getAllGoods should return a list of goods DTOs")
    void getAllGoods_ShouldReturnListOfGoods() {
        List<Goods> goodsList = Collections.singletonList(goods);
        List<GoodsDTO> expectedDTOs = Collections.singletonList(goodsDTO);

        when(goodsRepository.findAll()).thenReturn(goodsList);
        when(goodsMapper.toDto(any(Goods.class))).thenReturn(goodsDTO);

        List<GoodsDTO> result = goodsService.getAllGoods();

        assertEquals(expectedDTOs, result);
        verify(goodsRepository, times(1)).findAll();
        verify(goodsMapper, times(1)).toDto(goods);
    }

    @Test
    @DisplayName("getGoodsById should return goods DTO when found")
    void getGoodsById_ShouldReturnGoods_WhenFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        when(goodsMapper.toDto(any(Goods.class))).thenReturn(goodsDTO);

        GoodsDTO result = goodsService.getGoodsById(goodsId);

        assertEquals(goodsDTO, result);
        verify(goodsRepository, times(1)).findById(goodsId);
    }

    @Test
    @DisplayName("getGoodsById should throw GoodsNotFoundException when not found")
    void getGoodsById_ShouldThrowException_WhenNotFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        assertThrows(GoodsNotFoundException.class, () -> goodsService.getGoodsById(goodsId));
        verify(goodsMapper, never()).toDto(any(Goods.class));
    }

    @Test
    @DisplayName("createGoods should create and return a new goods DTO")
    void createGoods_ShouldCreateAndReturnGoods() {
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(false);
        when(goodsMapper.toEntity(any(GoodsDTO.class))).thenReturn(goods);
        when(goodsRepository.save(any(Goods.class))).thenReturn(goods);
        when(goodsMapper.toDto(any(Goods.class))).thenReturn(goodsDTO);

        GoodsDTO result = goodsService.createGoods(goodsDTO);

        assertEquals(goodsDTO, result);
        verify(goodsRepository).save(any(Goods.class));
    }

    @Test
    @DisplayName("createGoods should throw IllegalArgumentException when goods ART already exists")
    void createGoods_ShouldThrowException_WhenArtExists() {
        when(goodsRepository.existsByArt(goodsDTO.getArt())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> goodsService.createGoods(goodsDTO));
        verify(goodsMapper, never()).toEntity(any(GoodsDTO.class));
    }

    @Test
    @DisplayName("updateGoods should update and return updated goods DTO")
    void updateGoods_ShouldUpdateAndReturnGoods() {
        GoodsDTO updatedDTO = new GoodsDTO();
        updatedDTO.setArt("ART-002");
        updatedDTO.setName("Updated Mouse");

        goods.setId(UUID.randomUUID());

        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        when(goodsRepository.existsByArt(anyString())).thenReturn(false);
        when(goodsMapper.toEntity(any(GoodsDTO.class))).thenReturn(goods);
        when(goodsRepository.save(any(Goods.class))).thenReturn(goods);
        when(goodsMapper.toDto(goods)).thenReturn(updatedDTO);

        GoodsDTO result = goodsService.updateGoods(goodsId, updatedDTO);

        assertEquals(updatedDTO, result);

        verify(goodsRepository).findById(goodsId);
        verify(goodsRepository).existsByArt("ART-002");
        verify(goodsMapper).toEntity(updatedDTO);
        verify(goodsRepository).save(goods);
        verify(goodsMapper).toDto(goods);
        assertEquals(goodsId, goods.getId());
    }

    @Test
    @DisplayName("updateGoods should throw GoodsNotFoundException when goods not found")
    void updateGoods_ShouldThrowException_WhenNotFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        assertThrows(GoodsNotFoundException.class, () -> goodsService.updateGoods(goodsId, goodsDTO));
        verify(goodsRepository, never()).existsByArt(anyString());
        verify(goodsMapper, never()).toEntity(any(GoodsDTO.class));
        verify(goodsRepository, never()).save(any(Goods.class));
    }

    @Test
    @DisplayName("deleteGoods should delete goods when found")
    void deleteGoods_ShouldDeleteGoods_WhenFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.of(goods));
        doNothing().when(goodsRepository).deleteById(goodsId);

        goodsService.deleteGoods(goodsId);

        verify(goodsRepository).deleteById(goodsId);
    }

    @Test
    @DisplayName("deleteGoods should throw GoodsNotFoundException when goods not found")
    void deleteGoods_ShouldThrowException_WhenNotFound() {
        when(goodsRepository.findById(goodsId)).thenReturn(Optional.empty());

        assertThrows(GoodsNotFoundException.class, () -> goodsService.deleteGoods(goodsId));
        verify(goodsRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("searchGoods should return matching goods DTOs based on criteria")
    @SuppressWarnings("unchecked")
    void searchGoods_ShouldReturnMatchingGoods() {
        List<Goods> goodsList = Collections.singletonList(goods);

        when(goodsRepository.findAll(any(Specification.class))).thenReturn(goodsList);
        when(goodsMapper.toDto(any(Goods.class))).thenReturn(goodsDTO);

        List<GoodsDTO> result = goodsService.searchGoods(null, "ART-001", null, null, null, null, null, null, null, null, null);

        assertFalse(result.isEmpty());
        verify(goodsRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("searchGoods should return an empty list when no matches are found")
    @SuppressWarnings("unchecked")
    void searchGoods_ShouldReturnEmptyList_WhenNoMatches() {
        when(goodsRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<GoodsDTO> result = goodsService.searchGoods(null, "NonExistent", null, null, null, null, null, null, null, null, null);

        assertTrue(result.isEmpty());
        verify(goodsMapper, never()).toDto(any(Goods.class));
    }
}