package com.teamchallenge.easybuy.user.service;

import com.teamchallenge.easybuy.shop.service.security.ShopAccessGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RBACGuardService {

    private final ShopAccessGuard shopAccessGuard;

    public void requireCanManageProducts(UUID shopId) {
        shopAccessGuard.requireCanManageShop(shopId);
    }

    public void requireStoreOwner(UUID shopId) {
        shopAccessGuard.requireCanManageShop(shopId);
    }

    public void requireCanModerate() {
        shopAccessGuard.requireAdmin();
    }
}
