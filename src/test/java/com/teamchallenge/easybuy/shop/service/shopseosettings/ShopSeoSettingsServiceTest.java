package com.teamchallenge.easybuy.shop.service.shopseosettings;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopSeoSettings;
import com.teamchallenge.easybuy.shop.dto.ShopSeoSettingsDTO;
import com.teamchallenge.easybuy.shop.mapper.ShopSeoSettingsMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shopseosettings.ShopSeoSettingsRepository;
import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopSeoSettingsServiceTest {

    @Mock
    private ShopSeoSettingsRepository seoRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ShopSeoSettingsMapper mapper;
    @Mock
    private ShopAccessGuard accessGuard;

    @InjectMocks
    private ShopSeoSettingsService service;

    @Test
    @DisplayName("create should calculate seoScore and save")
    void create_valid_shouldCalculateSeoScore() {
        UUID shopId = UUID.randomUUID();

        Shop shop = new Shop();
        shop.setShopId(shopId);

        ShopSeoSettingsDTO request = new ShopSeoSettingsDTO();
        request.setMetaTitle("Title");
        request.setMetaDescription("Description");
        request.setCanonicalUrl("https://example.com/shop");

        ShopSeoSettings mapped = new ShopSeoSettings();
        mapped.setMetaTitle("Title");
        mapped.setMetaDescription("Description");
        mapped.setCanonicalUrl("https://example.com/shop");

        ShopSeoSettings saved = new ShopSeoSettings();
        ShopSeoSettingsDTO response = new ShopSeoSettingsDTO();
        response.setId(shopId);

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(seoRepository.existsById(shopId)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(mapped);
        when(seoRepository.save(any(ShopSeoSettings.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        service.create(shopId, request);

        ArgumentCaptor<ShopSeoSettings> captor = ArgumentCaptor.forClass(ShopSeoSettings.class);
        verify(seoRepository).save(captor.capture());
        assertNotNull(captor.getValue().getSeoScore());
    }

    @Test
    @DisplayName("update should throw when SEO settings do not exist")
    void update_notFound_shouldThrow() {
        UUID shopId = UUID.randomUUID();
        ShopSeoSettingsDTO dto = new ShopSeoSettingsDTO();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(seoRepository.findById(shopId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.update(shopId, dto));
    }

    @Test
    @DisplayName("delete should throw when SEO settings do not exist")
    void delete_notFound_shouldThrow() {
        UUID shopId = UUID.randomUUID();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(seoRepository.existsById(shopId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.delete(shopId));
    }
}

