package com.teamchallenge.easybuy.user.converter;

import com.teamchallenge.easybuy.openapi.dto.UpdateUserAccountRequest;
import com.teamchallenge.easybuy.openapi.dto.UserDto;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct converter for mapping between user-related data transfer objects and database entities.
 * <p>
 * This mapper integrates with {@link AddressDtoConverter} to handle nested address mappings.
 * Unmapped target properties are explicitly ignored to support partial updates and to prevent
 * sensitive entity fields (like passwords or roles) from being overwritten unintentionally.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AddressDtoConverter.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoConverter {

    /**
     * Converts a {@link UserEntity} database record into a {@link UserDto} for API responses.
     * <p>
     * <b>Notes:</b>
     * <ul>
     * <li>The nested address is mapped using the {@code toAddressDto} qualifier from {@code AddressDtoConverter}.</li>
     * <li>The {@code avatarLink} is explicitly ignored during basic mapping because it is constructed
     * dynamically by a dedicated service (e.g., UserAvatarLinkProvider) rather than stored as a raw entity field.</li>
     * </ul>
     *
     * @param entity the user database entity
     * @return the mapped user data transfer object
     */
    @Mapping(target = "address", source = "address", qualifiedByName = "toAddressDto")
    @Mapping(target = "avatarLink", ignore = true)
    UserDto toDto(final UserEntity entity);

    /**
     * Converts an {@link UpdateUserAccountRequest} into a {@link UserEntity}.
     * <p>
     * This method is primarily used to apply user profile modifications. The nested address
     * is mapped using the {@code toAddress} qualifier. Fields not present in the update request
     * (such as credentials, roles, and technical audit fields) are safely ignored by MapStruct.
     *
     * @param updateUserAccountRequest the incoming profile update payload
     * @return the corresponding user entity populated with the requested changes
     */
    @Mapping(target = "address", source = "address", qualifiedByName = "toAddress")
    UserEntity toEntity(final UpdateUserAccountRequest updateUserAccountRequest);
}