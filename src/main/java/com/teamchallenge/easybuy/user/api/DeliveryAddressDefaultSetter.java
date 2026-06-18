package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressDto;
import com.teamchallenge.easybuy.user.converter.DeliveryAddressDtoConverter;
import com.teamchallenge.easybuy.user.exception.DeliveryAddressNotFoundException;
import com.teamchallenge.easybuy.user.repository.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for managing the user's default delivery address.
 * <p>
 * Ensures that a user has exactly one address marked as 'default' at any time
 * by performing a bulk reset followed by a specific update.
 */
@Service
@RequiredArgsConstructor
public class DeliveryAddressDefaultSetter {

    private final DeliveryAddressRepository repository;
    private final DeliveryAddressDtoConverter converter;

    /**
     * Updates the user's default delivery address.
     * <p>
     * <b>Process:</b>
     * <ol>
     * <li>Validates that the address exists and belongs to the specified user.</li>
     * <li>Clears the default flag for all existing addresses of the user.</li>
     * <li>Sets the default flag to {@code true} for the target address.</li>
     * </ol>
     *
     * @param userId    the unique identifier of the user
     * @param addressId the unique identifier of the address to be set as default
     * @return the updated {@link DeliveryAddressDto}
     * @throws DeliveryAddressNotFoundException if the address is not found or does not belong to the user
     */
    @Transactional
    public DeliveryAddressDto setDefault(UUID userId, UUID addressId) {
        var entity = repository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(addressId));

        // Atomically clear current default status for the user
        repository.clearDefaultForUser(userId);

        entity.setDefault(true);
        return converter.toDto(repository.save(entity));
    }
}