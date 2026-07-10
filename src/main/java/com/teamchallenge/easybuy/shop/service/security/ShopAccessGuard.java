package com.teamchallenge.easybuy.shop.service.security;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.user.entity.MembershipStatus;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.repository.StoreMembershipRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShopAccessGuard {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final StoreMembershipRepository storeMembershipRepository;

    public void requireCanManageShop(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found: " + shopId));
        requireCanManageShop(shop);
    }

    public void requireCanManageShop(Shop shop) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return;
        if (hasAuthority(auth, "ROLE_ADMIN")) return;

        UserEntity currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UserEntity not found"));

        boolean hasAccess = storeMembershipRepository.existsByUser_IdAndStoreIdAndStatus(
                currentUser.getId(), shop.getShopId(), MembershipStatus.ACTIVE);

        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && hasAuthority(auth, "ROLE_ADMIN");
    }

    public void requireAdmin() {
        if (!isCurrentUserAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin rights required");
        }
    }

    public UUID getCurrentUserIdOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return userRepository.findByEmail(auth.getName())
                .map(user -> (UUID) user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private boolean hasAuthority(Authentication auth, String authority) {
        return auth.getAuthorities().stream().anyMatch(a -> authority.equals(a.getAuthority()));
    }
}