package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressDto;
import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressRequest;
import com.teamchallenge.easybuy.user.converter.DeliveryAddressDtoConverter;
import com.teamchallenge.easybuy.user.exception.DeliveryAddressNotFoundException;
import com.teamchallenge.easybuy.user.repository.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for updating existing delivery addresses.
 * <p>
 * Ensures that modifications are restricted to valid user-owned addresses and
 * updates specific fields while preserving internal metadata like IDs and
 * default-status flags.
 */
@Service
@RequiredArgsConstructor
public class DeliveryAddressUpdater {

    private final DeliveryAddressRepository repository;
    private final DeliveryAddressDtoConverter converter;

    /**
     * Updates an existing delivery address for a specific user.
     * <p>
     * <b>Security Note:</b> Performs an existence check against both the address ID
     * and the user ID to ensure that users cannot modify addresses belonging
     * to other accounts (ID-enumeration protection).
     *
     * @param userId    the unique identifier of the user
     * @param addressId the unique identifier of the address to update
     * @param request   the payload containing the new address field values
     * @return the updated {@link DeliveryAddressDto}
     * @throws DeliveryAddressNotFoundException if the address cannot be found or does not belong to the user
     */
    @Transactional
    public DeliveryAddressDto update(UUID userId, UUID addressId, DeliveryAddressRequest request) {
        var entity = repository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(addressId));

        // Explicit field mapping ensures only allowed fields are modified
        entity.setLabel(request.getLabel());
        entity.setLine(request.getLine());
        entity.setCity(request.getCity());
        entity.setCountry(request.getCountry());
        entity.setPostcode(request.getPostcode());

        return converter.toDto(repository.save(entity));
    }
}