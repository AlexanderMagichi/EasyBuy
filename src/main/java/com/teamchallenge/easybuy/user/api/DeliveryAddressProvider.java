package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressDto;
import com.teamchallenge.easybuy.user.converter.DeliveryAddressDtoConverter;
import com.teamchallenge.easybuy.user.repository.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for retrieving delivery addresses associated with a user profile.
 */
@Service
@RequiredArgsConstructor
public class DeliveryAddressProvider {

    private final DeliveryAddressRepository repository;
    private final DeliveryAddressDtoConverter converter;

    /**
     * Retrieves all delivery addresses belonging to the specified user.
     * <p>
     * The operation is performed within a read-only transaction to optimize
     * performance by avoiding dirty checking of entities.
     *
     * @param userId the unique identifier of the user whose addresses are to be retrieved
     * @return a list of {@link DeliveryAddressDto} objects representing the user's saved addresses
     */
    @Transactional(readOnly = true)
    public List<DeliveryAddressDto> getAll(UUID userId) {
        return repository.findAllByUserId(userId).stream()
                .map(converter::toDto)
                .toList();
    }
}