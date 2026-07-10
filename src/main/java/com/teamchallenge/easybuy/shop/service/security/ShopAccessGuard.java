package com.teamchallenge.easybuy.shop.service.security;

import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Centralized ownership checks for shop-related operations.
 *
 * <p>Rules:
 * <ul>
 *   <li>ADMIN can manage any shop.</li>
 *   <li>SELLER can only manage own shop(s).</li>
 *   <li>If no authentication is present (e.g. some tests), checks are skipped.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class ShopAccessGuard {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public void requireCanManageShop(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shop not found: " + shopId));
        requireCanManageShop(shop);
    }

    public void requireCanManageShop(Shop shop) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        if (hasAuthority(authentication, "ROLE_SUPER_ADMIN") || hasAuthority(authentication, "ROLE_ADMIN")) {
            return;
        }

        if (!hasAuthority(authentication, "ROLE_SELLER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only seller or admin can manage shop resources");
        }

        UserEntity currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user is not found"));

        UUID sellerId = shop.getSeller() != null ? shop.getSeller().getId() : null;
        if (sellerId == null || !sellerId.equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can manage only your own shops");
        }
    }

    public void requireAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }

        if (!isCurrentUserAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role is required for this action");
        }
    }

    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && (hasAuthority(authentication, "ROLE_SUPER_ADMIN") || hasAuthority(authentication, "ROLE_ADMIN"));
    }

    public boolean isCurrentUserSeller() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && hasAuthority(authentication, "ROLE_SELLER");
    }

    public UserEntity getCurrentSellerOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user is not found"));
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }
}
