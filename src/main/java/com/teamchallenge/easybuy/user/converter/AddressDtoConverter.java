package com.teamchallenge.easybuy.user.converter;

import com.teamchallenge.easybuy.openapi.dto.AddressDto;
import com.teamchallenge.easybuy.user.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct converter for translating between {@link Address} entities and {@link AddressDto} objects.
 * <p>
 * This interface is implemented automatically by MapStruct at compile time and registered as a Spring Bean.
 * Unmapped target properties are ignored to prevent mapping errors during partial updates.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressDtoConverter {

    /**
     * Converts an {@link Address} database entity into an {@link AddressDto} for API responses.
     * <p>
     * Annotated with {@code @Named("toAddressDto")} so it can be explicitly referenced
     * and reused by other composite converters (e.g., UserDtoConverter).
     *
     * @param entity the address entity to convert
     * @return the mapped data transfer object
     */
    @Named("toAddressDto")
    AddressDto toDto(final Address entity);

    /**
     * Converts an {@link AddressDto} received from an API request into an {@link Address} entity.
     * <p>
     * Annotated with {@code @Named("toAddress")} so it can be explicitly referenced
     * and reused by other composite converters.
     *
     * @param dto the data transfer object to convert
     * @return the mapped database entity
     */
    @Named("toAddress")
    Address toEntity(final AddressDto dto);
}