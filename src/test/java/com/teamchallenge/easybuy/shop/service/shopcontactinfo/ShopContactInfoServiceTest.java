package com.teamchallenge.easybuy.shop.service.shopcontactinfo;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopContactInfo;
import com.teamchallenge.easybuy.shop.dto.shopcontact.ShopContactInfoDTO;
import com.teamchallenge.easybuy.shop.mapper.ShopContactInfoMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shopcontact.ShopContactInfoRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopContactInfoServiceTest {

    @Mock
    private ShopContactInfoRepository contactInfoRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ShopContactInfoMapper mapper;
    @Mock
    private ShopAccessGuard accessGuard;

    @InjectMocks
    private ShopContactInfoService service;

    @Test
    @DisplayName("create should save mapped entity linked to shop")
    void create_valid_shouldSaveEntity() {
        UUID shopId = UUID.randomUUID();

        Shop shop = new Shop();
        shop.setShopId(shopId);

        ShopContactInfoDTO request = new ShopContactInfoDTO();
        request.setContactEmail("contact@example.com");
        request.setContactPhone("+380931112233");

        ShopContactInfo mapped = new ShopContactInfo();
        ShopContactInfo saved = new ShopContactInfo();
        saved.setShop(shop);

        ShopContactInfoDTO response = new ShopContactInfoDTO();
        response.setContactEmail("contact@example.com");

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(contactInfoRepository.existsByShop_ShopIdAndActiveTrue(shopId)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(mapped);
        when(contactInfoRepository.save(any(ShopContactInfo.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        ShopContactInfoDTO result = service.create(shopId, request);

        ArgumentCaptor<ShopContactInfo> captor = ArgumentCaptor.forClass(ShopContactInfo.class);
        verify(contactInfoRepository).save(captor.capture());
        assertEquals(shop, captor.getValue().getShop());
        assertEquals("contact@example.com", result.getContactEmail());
    }

    @Test
    @DisplayName("create should throw when active contact info already exists")
    void create_duplicate_shouldThrow() {
        UUID shopId = UUID.randomUUID();
        ShopContactInfoDTO request = new ShopContactInfoDTO();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(new Shop()));
        when(contactInfoRepository.existsByShop_ShopIdAndActiveTrue(shopId)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.create(shopId, request));
    }

    @Test
    @DisplayName("verify should mark entity as verified")
    void verify_shouldMarkVerified() {
        UUID shopId = UUID.randomUUID();

        ShopContactInfo entity = new ShopContactInfo();
        entity.setVerified(false);

        doNothing().when(accessGuard).requireAdmin();
        when(contactInfoRepository.findByShop_ShopId(shopId)).thenReturn(Optional.of(entity));
        when(contactInfoRepository.save(entity)).thenReturn(entity);

        service.verify(shopId);

        assertTrue(entity.isVerified());
        verify(contactInfoRepository).save(entity);
    }
}

