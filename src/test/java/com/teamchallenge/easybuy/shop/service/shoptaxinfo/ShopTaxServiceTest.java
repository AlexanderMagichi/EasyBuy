package com.teamchallenge.easybuy.shop.service.shoptaxinfo;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.entity.ShopTaxInfo;
import com.teamchallenge.easybuy.shop.dto.shoptaxinfo.ShopTaxInfoDTO;
import com.teamchallenge.easybuy.shop.mapper.ShopTaxMapper;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.shop.repository.shoptaxrepository.ShopTaxRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopTaxServiceTest {

    @Mock
    private ShopTaxRepository taxRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ShopTaxMapper mapper;
    @Mock
    private ShopAccessGuard accessGuard;

    @InjectMocks
    private ShopTaxService service;

    @Test
    @DisplayName("create should set shop and id before save")
    void create_valid_shouldSetIdAndShop() {
        UUID shopId = UUID.randomUUID();

        Shop shop = new Shop();
        shop.setShopId(shopId);

        ShopTaxInfoDTO request = new ShopTaxInfoDTO();
        request.setTaxId("123");
        request.setTaxpayerType("BUSINESS");
        request.setLegalName("Legal Name");

        ShopTaxInfo mapped = new ShopTaxInfo();
        ShopTaxInfo saved = new ShopTaxInfo();
        saved.setId(shopId);

        ShopTaxInfoDTO response = new ShopTaxInfoDTO();
        response.setId(shopId);

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
        when(taxRepository.existsById(shopId)).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(mapped);
        when(taxRepository.save(any(ShopTaxInfo.class))).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(response);

        ShopTaxInfoDTO result = service.create(shopId, request);

        ArgumentCaptor<ShopTaxInfo> captor = ArgumentCaptor.forClass(ShopTaxInfo.class);
        verify(taxRepository).save(captor.capture());
        assertEquals(shopId, captor.getValue().getId());
        assertEquals(shop, captor.getValue().getShop());
        assertEquals(shopId, result.getId());
    }

    @Test
    @DisplayName("update should throw when tax info does not exist")
    void update_notFound_shouldThrow() {
        UUID shopId = UUID.randomUUID();
        ShopTaxInfoDTO request = new ShopTaxInfoDTO();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(taxRepository.findById(shopId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.update(shopId, request));
    }

    @Test
    @DisplayName("delete should throw when tax info does not exist")
    void delete_notFound_shouldThrow() {
        UUID shopId = UUID.randomUUID();

        doNothing().when(accessGuard).requireCanManageShop(shopId);
        when(taxRepository.existsById(shopId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.delete(shopId));
    }
}

