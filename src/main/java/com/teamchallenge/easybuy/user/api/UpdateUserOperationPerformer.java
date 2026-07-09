package com.teamchallenge.easybuy.user.api;

import com.teamchallenge.easybuy.openapi.dto.AddressDto;
import com.teamchallenge.easybuy.openapi.dto.UpdateUserAccountRequest;
import com.teamchallenge.easybuy.openapi.dto.UserDto;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import com.teamchallenge.easybuy.user.converter.AddressDtoConverter;
import com.teamchallenge.easybuy.user.converter.UserDtoConverter;
import com.teamchallenge.easybuy.user.entity.Address;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import com.teamchallenge.easybuy.user.validator.PutUsersRequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for orchestrating the update of a user's account profile.
 * <p>
 * This performer handles the end-to-end flow: from extracting the authenticated user ID
 * and running business validation to mapping DTO changes onto the persistent domain entity.
 */
@Service
@RequiredArgsConstructor
public class UpdateUserOperationPerformer {

    private final SingleUserProvider singleUserProvider;
    private final UserRepository userCrudRepository;
    private final UserDtoConverter userDtoConverter;
    private final AddressDtoConverter addressDtoConverter;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final PutUsersRequestValidator putUsersRequestValidator;

    /**
     * Updates the profile information of the currently authenticated user.
     * <p>
     * <b>Orchestration Flow:</b>
     * <ol>
     * <li>Extracts user identity from the security context.</li>
     * <li>Performs business-level validation (name length, phone format, age restrictions).</li>
     * <li>Fetches the current {@link UserEntity} from the database.</li>
     * <li>Converts the address DTO into an entity and updates the user record.</li>
     * <li>Persists the changes and returns the updated {@link UserDto}.</li>
     * </ol>
     *
     * @param updateUserAccountRequest the incoming request payload with updated fields
     * @return the resulting {@link UserDto} reflecting the saved state
     * @throws com.teamchallenge.easybuy.user.exception.PutUsersBadRequestException if validation fails
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public UserDto updateUser(final UpdateUserAccountRequest updateUserAccountRequest) {
        UUID userId = securityPrincipalProvider.getUserId();

        AddressDto addressDto = updateUserAccountRequest.getAddress();
        String birthDateStr = updateUserAccountRequest.getBirthDate() != null
                ? updateUserAccountRequest.getBirthDate().toString() : null;

        // Perform business validation before modifying the entity state
        putUsersRequestValidator.validate(
                updateUserAccountRequest.getFirstName(),
                updateUserAccountRequest.getLastName(),
                updateUserAccountRequest.getPhoneNumber(),
                birthDateStr,
                addressDto
        );

        UserEntity userEntity = singleUserProvider.getUserEntityById(userId);
        Address addressEntity = addressDtoConverter.toEntity(addressDto);

        // Update domain entity fields
        userEntity.setFirstName(updateUserAccountRequest.getFirstName());
        userEntity.setLastName(updateUserAccountRequest.getLastName());
        userEntity.setBirthDate(updateUserAccountRequest.getBirthDate());
        userEntity.setPhoneNumber(updateUserAccountRequest.getPhoneNumber());
        userEntity.setAddress(addressEntity);

        UserEntity userEntityWithId = userCrudRepository.save(userEntity);
        return userDtoConverter.toDto(userEntityWithId);
    }
}