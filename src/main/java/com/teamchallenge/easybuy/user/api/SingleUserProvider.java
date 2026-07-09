package com.teamchallenge.easybuy.user.api;


import com.teamchallenge.easybuy.openapi.dto.UserDto;
import com.teamchallenge.easybuy.user.converter.UserDtoConverter;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.exception.UserNotFoundException;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for retrieving individual user information.
 * <p>
 * Provides methods to fetch users by their unique identifier or email,
 * supporting both API-facing data models and internal domain entities.
 */
@Service
@RequiredArgsConstructor
public class SingleUserProvider {

    private final UserRepository userCrudRepository;
    private final UserDtoConverter userDtoConverter;
    private final UserAvatarLinkUpdater userAvatarLinkUpdater;

    /**
     * Retrieves a {@link UserDto} by the user's unique identifier.
     * <p>
     * <b>Enrichment:</b> The retrieved DTO is automatically enriched with a
     * dynamically generated avatar link via {@link UserAvatarLinkUpdater}.
     *
     * @param userId the unique identifier of the user
     * @return the enriched {@link UserDto}
     * @throws UserNotFoundException if no user is found with the given ID
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(final UUID userId) throws UserNotFoundException {
        return userCrudRepository.findById(userId)
                .map(userDtoConverter::toDto)
                .map(userAvatarLinkUpdater::update)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Retrieves the raw {@link UserEntity} for internal domain logic operations.
     * <p>
     * Should be used for authentication, password updates, or any logic
     * requiring direct interaction with the database entity.
     *
     * @param userId the unique identifier of the user
     * @return the persistent {@link UserEntity}
     * @throws UserNotFoundException if no user is found with the given ID
     */
    @Transactional(readOnly = true)
    public UserEntity getUserEntityById(final UUID userId) throws UserNotFoundException {
        return userCrudRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    /**
     * Retrieves a {@link UserDto} by the user's email address.
     *
     * @param email the email address of the user
     * @return the {@link UserDto} model
     * @throws UserNotFoundException if no user is found with the given email
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(final String email) throws UserNotFoundException {
        return userCrudRepository.findByEmail(email)
                .map(userDtoConverter::toDto)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}