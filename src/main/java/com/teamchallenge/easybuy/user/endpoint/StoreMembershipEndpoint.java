package com.teamchallenge.easybuy.user.endpoint;

import com.teamchallenge.easybuy.user.converter.StoreMembershipDtoConverter;
import com.teamchallenge.easybuy.user.dto.CreateStoreMembershipRequest;
import com.teamchallenge.easybuy.user.dto.StoreMembershipResponse;
import com.teamchallenge.easybuy.user.service.RBACGuardService;
import com.teamchallenge.easybuy.user.service.StoreMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Store-scoped role delegation: an owner (or super-admin) manages the team of a
 * specific store by granting / revoking {@code MANAGER} and {@code CONTENT_MANAGER}
 * memberships.
 *
 * <p>Authorization is two-staged (users-rbac.md §9):
 * <ol>
 * <li>{@code @PreAuthorize} gates the section to globally privileged roles;</li>
 * <li>{@link RBACGuardService} confirms the caller actually owns <b>this</b> store
 * (or is a super-admin) before the command runs.</li>
 * </ol>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Store memberships", description = "Delegation of store-scoped roles (MANAGER / CONTENT_MANAGER).")
@RequestMapping(StoreMembershipEndpoint.API_STORE_MEMBERSHIPS)
public class StoreMembershipEndpoint {

    public static final String API_STORE_MEMBERSHIPS = "/api/v1/stores/{storeId}/memberships";

    private final StoreMembershipService membershipService;
    private final RBACGuardService rbacGuard;
    private final StoreMembershipDtoConverter converter;

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'SUPER_ADMIN')")
    @Operation(summary = "Grant a store role to a user",
            description = "Grants a specific role (MANAGER/CONTENT_MANAGER) to a user within the store. Only the store owner or super-admin can perform this action.")
    @ApiResponse(responseCode = "201", description = "Membership successfully created")
    public ResponseEntity<StoreMembershipResponse> grant(@PathVariable UUID storeId,
                                                         @Valid @RequestBody CreateStoreMembershipRequest request) {
        rbacGuard.requireCanDelegateRoles(storeId);
        UUID grantedBy = rbacGuard.currentUserId();
        log.info("store_membership.endpoint.grant: storeId={}, targetUserId={}, role={}, grantedBy={}",
                storeId, request.userId(), request.role(), grantedBy);
        var membership = membershipService.grant(storeId, request, grantedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(converter.toResponse(membership));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SELLER', 'SUPER_ADMIN')")
    @Operation(summary = "List store memberships",
            description = "Retrieves a list of all active team members for the specified store.")
    public ResponseEntity<List<StoreMembershipResponse>> list(@PathVariable UUID storeId) {
        rbacGuard.requireStoreOwner(storeId);
        var response = membershipService.listActiveForStore(storeId).stream()
                .map(converter::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{membershipId}")
    @PreAuthorize("hasAnyRole('SELLER', 'SUPER_ADMIN')")
    @Operation(summary = "Revoke a store membership",
            description = "Soft-deletes a membership record, effectively removing the user's rights in the store.")
    @ApiResponse(responseCode = "204", description = "Membership revoked")
    public ResponseEntity<Void> revoke(@PathVariable UUID storeId, @PathVariable UUID membershipId) {
        rbacGuard.requireCanDelegateRoles(storeId);
        UUID revokedBy = rbacGuard.currentUserId();
        log.info("store_membership.endpoint.revoke: storeId={}, membershipId={}, revokedBy={}",
                storeId, membershipId, revokedBy);
        membershipService.revoke(membershipId, revokedBy);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{membershipId}/suspend")
    @PreAuthorize("hasAnyRole('SELLER', 'SUPER_ADMIN')")
    @Operation(summary = "Suspend a store membership",
            description = "Temporarily disables a user's rights within the store without removing the record.")
    public ResponseEntity<StoreMembershipResponse> suspend(@PathVariable UUID storeId,
                                                           @PathVariable UUID membershipId) {
        rbacGuard.requireCanDelegateRoles(storeId);
        var membership = membershipService.suspend(membershipId, rbacGuard.currentUserId());
        return ResponseEntity.ok(converter.toResponse(membership));
    }

    @PatchMapping("/{membershipId}/reactivate")
    @PreAuthorize("hasAnyRole('SELLER', 'SUPER_ADMIN')")
    @Operation(summary = "Reactivate a store membership",
            description = "Restores access for a previously suspended membership.")
    public ResponseEntity<StoreMembershipResponse> reactivate(@PathVariable UUID storeId,
                                                              @PathVariable UUID membershipId) {
        rbacGuard.requireCanDelegateRoles(storeId);
        var membership = membershipService.reactivate(membershipId, rbacGuard.currentUserId());
        return ResponseEntity.ok(converter.toResponse(membership));
    }
}