package com.teamchallenge.easybuy.user.endpoint;

import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressDto;
import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressRequest;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import com.teamchallenge.easybuy.user.api.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing user delivery addresses.
 * Provides endpoints for CRUD operations and default address configuration.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(UserEndpoint.API_CUSTOMERS + "/addresses")
@Tag(name = "Delivery Addresses", description = "Operations for managing user delivery addresses")
public class DeliveryAddressEndpoint {

    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final DeliveryAddressProvider provider;
    private final DeliveryAddressCreator creator;
    private final DeliveryAddressUpdater updater;
    private final DeliveryAddressDeleter deleter;
    private final DeliveryAddressDefaultSetter defaultSetter;

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Retrieves a list of all saved delivery addresses for the current user.")
    public ResponseEntity<List<DeliveryAddressDto>> getAll() {
        var userId = securityPrincipalProvider.getUserId();
        log.info("delivery_address.get_all: userId={}", userId);
        return ResponseEntity.ok(provider.getAll(userId));
    }

    @PostMapping
    @Operation(summary = "Create an address", description = "Adds a new delivery address to the user's profile.")
    public ResponseEntity<DeliveryAddressDto> create(@Valid @RequestBody DeliveryAddressRequest request) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("delivery_address.create: userId={}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(creator.create(userId, request));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update an address", description = "Updates details of an existing delivery address.")
    public ResponseEntity<DeliveryAddressDto> update(@PathVariable UUID addressId,
                                                     @Valid @RequestBody DeliveryAddressRequest request) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("delivery_address.update: userId={}, addressId={}", userId, addressId);
        return ResponseEntity.ok(updater.update(userId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete an address", description = "Removes a specific delivery address from the user's account.")
    public ResponseEntity<Void> delete(@PathVariable UUID addressId) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("delivery_address.delete: userId={}, addressId={}", userId, addressId);
        deleter.delete(userId, addressId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{addressId}/default")
    @Operation(summary = "Set as default", description = "Marks a specific delivery address as the default one for the user.")
    public ResponseEntity<DeliveryAddressDto> setDefault(@PathVariable UUID addressId) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("delivery_address.set_default: userId={}, addressId={}", userId, addressId);
        return ResponseEntity.ok(defaultSetter.setDefault(userId, addressId));
    }
}