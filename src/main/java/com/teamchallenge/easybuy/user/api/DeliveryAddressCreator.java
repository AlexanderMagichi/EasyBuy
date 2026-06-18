package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressDto;
import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressRequest;
import com.teamchallenge.easybuy.user.converter.DeliveryAddressDtoConverter;
import com.teamchallenge.easybuy.user.exception.UserNotFoundException;
import com.teamchallenge.easybuy.user.repository.DeliveryAddressRepository;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for creating new delivery addresses for a user.
 * <p>
 * Enforces the core business rule that the first address added to a user's
 * profile is automatically assigned as their default delivery address.
 */
@Service
@RequiredArgsConstructor
public class DeliveryAddressCreator {

    private final DeliveryAddressRepository addressRepository;
    private final UserRepository userRepository;
    private final DeliveryAddressDtoConverter converter;

    /**
     * Creates and persists a new delivery address for the specified user.
     * <p>
     * <b>Business Logic:</b> Checks the user's existing addresses. If the user currently
     * has no saved addresses, the newly created address is automatically marked as the default.
     *
     * @param userId  the unique identifier of the user adding the address
     * @param request the payload containing the delivery address details (country, city, line, etc.)
     * @return the newly created and saved delivery address as a DTO
     * @throws UserNotFoundException if the provided user ID does not exist in the system
     */
    @Transactional
    public DeliveryAddressDto create(UUID userId, DeliveryAddressRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        var entity = converter.toEntity(request);
        entity.setUser(user);

        boolean isFirst = addressRepository.findAllByUserId(userId).isEmpty();
        entity.setDefault(isFirst);

        return converter.toDto(addressRepository.save(entity));
    }
}