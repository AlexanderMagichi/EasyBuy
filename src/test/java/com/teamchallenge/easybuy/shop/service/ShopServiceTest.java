package com.teamchallenge.easybuy.shop.service;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.event.ShopCreatedEvent;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.shop.dto.ShopDTO;
import com.teamchallenge.easybuy.shop.mapper.ShopMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import com.teamchallenge.easybuy.shop.service.shopcontactinfo.ShopContactInfoService;
import com.teamchallenge.easybuy.shop.service.shopseosettings.ShopSeoSettingsService;
import com.teamchallenge.easybuy.shop.service.shoptaxinfo.ShopTaxService;
import com.teamchallenge.easybuy.shop.service.validation.ShopValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ShopMapper shopMapper;
    @Mock
    private UserRepository sellerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ShopValidationService validationService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private ShopAccessGuard accessGuard;
    @Mock
    private ShopContactInfoService shopContactInfoService;
    @Mock
    private ShopTaxService shopTaxService;
    @Mock
    private ShopSeoSettingsService shopSeoSettingsService;

    @InjectMocks
    private ShopService shopService;

    @Test
    @DisplayName("createShop should bind current seller and apply defaults in SELLER context")
    void createShop_asSeller_setsSellerAndDefaults() {
        UUID shopId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();

        UserEntity seller = new UserEntity();
        seller.setId(sellerId);
        seller.setEmail("seller@example.com");

        ShopDTO input = new ShopDTO();
        input.setShopName("New Shop");
        input.setShopDescription("Description");

        Shop mapped = new Shop();
        mapped.setShopName("New Shop");
        mapped.setShopDescription("Description");

        ShopDTO output = new ShopDTO();
        output.setShopId(shopId);

        when(accessGuard.isCurrentUserSeller()).thenReturn(true);
        when(accessGuard.isCurrentUserAdmin()).thenReturn(false);
        when(accessGuard.getCurrentSellerOrThrow()).thenReturn(seller);
        when(shopMapper.toEntity(any(ShopDTO.class))).thenReturn(mapped);
        when(shopRepository.existsBySlug(anyString())).thenReturn(false);
        when(shopRepository.save(any(Shop.class))).thenAnswer(invocation -> {
            Shop s = invocation.getArgument(0);
            s.setShopId(shopId);
            return s;
        });
        when(shopMapper.toDto(any(Shop.class))).thenReturn(output);

        ShopDTO created = shopService.createShop(input);

        ArgumentCaptor<Shop> captor = ArgumentCaptor.forClass(Shop.class);
        verify(shopRepository).save(captor.capture());
        Shop saved = captor.getValue();

        assertEquals(shopId, created.getShopId());
        assertEquals(seller, saved.getSeller());
        assertEquals(Shop.ShopStatus.PENDING, saved.getShopStatus());
        assertEquals("UAH", saved.getCurrency());
        assertEquals("uk", saved.getLanguage());
        assertEquals("Europe/Kyiv", saved.getTimezone());
        assertTrue(saved.getSlug() != null && !saved.getSlug().isBlank());
        verify(eventPublisher).publishEvent(any(ShopCreatedEvent.class));
    }

    @Test
    @DisplayName("updateShop should keep protected fields unchanged for SELLER")
    void updateShop_asSeller_keepsProtectedFields() {
        UUID shopId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        UserEntity owner = new UserEntity();
        owner.setId(ownerId);

        Shop existing = new Shop();
        existing.setShopId(shopId);
        existing.setShopName("Old Name");
        existing.setShopDescription("Old Desc");
        existing.setShopStatus(Shop.ShopStatus.ACTIVE);
        existing.setVerified(true);
        existing.setFeatured(true);
        existing.setRejectionReason("old reason");
        existing.setModeratorNotes("old notes");
        existing.setSeller(owner);

        ShopDTO updates = new ShopDTO();
        updates.setShopName("New Name");
        updates.setShopDescription("New Desc");
        updates.setShopStatus(Shop.ShopStatus.BANNED);
        updates.setSellerId(UUID.randomUUID());

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(existing));
        when(accessGuard.isCurrentUserSeller()).thenReturn(true);
        when(accessGuard.isCurrentUserAdmin()).thenReturn(false);
        doNothing().when(accessGuard).requireCanManageShop(existing);
        doNothing().when(validationService).validateForUpdate(existing, updates);
        doAnswer(invocation -> {
            ShopDTO dto = invocation.getArgument(0);
            Shop entity = invocation.getArgument(1);
            entity.setShopName(dto.getShopName());
            entity.setShopDescription(dto.getShopDescription());
            entity.setShopStatus(dto.getShopStatus());
            return null;
        }).when(shopMapper).updateEntityFromDto(any(ShopDTO.class), any(Shop.class));
        when(shopRepository.save(existing)).thenReturn(existing);
        when(shopMapper.toDto(existing)).thenReturn(new ShopDTO());

        shopService.updateShop(shopId, updates);

        assertEquals(Shop.ShopStatus.ACTIVE, existing.getShopStatus());
        assertEquals(owner, existing.getSeller());
        assertEquals("old reason", existing.getRejectionReason());
        assertEquals("old notes", existing.getModeratorNotes());
    }

    @Test
    @DisplayName("deleteShop should throw for non-admin")
    void deleteShop_nonAdmin_throws() {
        UUID shopId = UUID.randomUUID();
        Shop shop = new Shop();
        shop.setShopId(shopId);

        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        doNothing().when(accessGuard).requireCanManageShop(shop);
        when(accessGuard.isCurrentUserAdmin()).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> shopService.deleteShop(shopId));
        verify(shopRepository, never()).delete(any(Shop.class));
    }

    @Test
    @DisplayName("getShopsBySeller should reject access to foreign seller shops")
    void getShopsBySeller_foreignSeller_throws() {
        UUID requestedSellerId = UUID.randomUUID();
        UUID currentSellerId = UUID.randomUUID();

        UserEntity currentSeller = new UserEntity();
        currentSeller.setId(currentSellerId);

        when(accessGuard.isCurrentUserSeller()).thenReturn(true);
        when(accessGuard.isCurrentUserAdmin()).thenReturn(false);
        when(accessGuard.getCurrentSellerOrThrow()).thenReturn(currentSeller);

        assertThrows(IllegalArgumentException.class, () -> {
            shopService.getShopsBySeller(requestedSellerId, PageRequest.of(0, 5));
        });
    }

    @Test
    @DisplayName("getShopsBySeller should return page for owner seller")
    void getShopsBySeller_ownerSeller_returnsPage() {
        UUID sellerId = UUID.randomUUID();
        UserEntity currentSeller = new UserEntity();
        currentSeller.setId(sellerId);

        Shop shop = new Shop();
        shop.setShopId(UUID.randomUUID());
        Page<Shop> page = new PageImpl<>(List.of(shop));

        when(accessGuard.isCurrentUserSeller()).thenReturn(true);
        when(accessGuard.isCurrentUserAdmin()).thenReturn(false);
        when(accessGuard.getCurrentSellerOrThrow()).thenReturn(currentSeller);
        when(shopRepository.findBySellerId(sellerId, PageRequest.of(0, 5))).thenReturn(page);
        when(shopMapper.toDto(shop)).thenReturn(new ShopDTO());

        Page<ShopDTO> result = shopService.getShopsBySeller(sellerId, PageRequest.of(0, 5));

        assertEquals(1, result.getTotalElements());
    }
}

