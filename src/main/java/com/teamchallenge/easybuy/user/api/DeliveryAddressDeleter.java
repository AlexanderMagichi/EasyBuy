package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.user.exception.DeliveryAddressNotFoundException;
import com.teamchallenge.easybuy.user.repository.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for deleting delivery addresses associated with a user profile.
 */
@Service
@RequiredArgsConstructor
public class DeliveryAddressDeleter {

    private final DeliveryAddressRepository repository;

    /**
     * Deletes a delivery address for the given user.
     * <p>
     * <b>Security Note:</b> This method validates that the address belongs to the
     * specified user before attempting deletion. This prevents unauthorized users
     * from deleting addresses belonging to other accounts.
     *
     * @param userId    the unique identifier of the user
     * @param addressId the unique identifier of the address to delete
     * @throws DeliveryAddressNotFoundException if the address does not exist or does not belong to the user
     */
    @Transactional
    public void delete(UUID userId, UUID addressId) {
        var entity = repository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(addressId));
        repository.delete(entity);
    }
}