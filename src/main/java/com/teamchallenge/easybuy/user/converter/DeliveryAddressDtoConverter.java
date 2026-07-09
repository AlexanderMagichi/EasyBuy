package com.teamchallenge.easybuy.user.converter;

import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressDto;
import com.teamchallenge.easybuy.openapi.dto.DeliveryAddressRequest;
import com.teamchallenge.easybuy.user.entity.DeliveryAddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct converter for translating delivery address objects.
 * Handles the mapping between incoming API requests, database entities, and outgoing DTO responses.
 * <p>
 * <b>Note on boolean mapping:</b> Lombok generates the getter {@code isDefault()} and setter {@code setDefault()}
 * for the {@code isDefault} field in {@link DeliveryAddressEntity}. According to JavaBeans standards,
 * this makes the property name "default", not "isDefault". MapStruct strictly follows this standard.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @org.mapstruct.Builder(disableBuilder = true)
)
public interface DeliveryAddressDtoConverter {

    /**
     * Converts a database entity into a DTO for API responses.
     * <p>
     * Explicitly maps the 'default' property from the entity (JavaBeans standard for boolean isDefault)
     * to the 'isDefault' field in the OpenAPI-generated DTO.
     *
     * @param entity the delivery address database entity
     * @return the mapped delivery address DTO
     */
    @Mapping(target = "isDefault", source = "default")
    DeliveryAddressDto toDto(DeliveryAddressEntity entity);

    /**
     * Converts an incoming delivery address request into a database entity.
     * <p>
     * <b>Security and Business Logic Constraints:</b>
     * <ul>
     * <li>{@code id} is ignored to prevent clients from explicitly setting or overriding the primary key.</li>
     * <li>{@code user} is ignored because the entity-user relationship must be securely established
     * via the authentication context in the service layer, not from the request payload.</li>
     * <li>{@code default} (representing the isDefault flag) is ignored because the default address
     * calculation and reassignment is handled dynamically by business rules in the service layer.</li>
     * </ul>
     *
     * @param request the incoming data transfer object containing address details
     * @return the mapped database entity with restricted fields left empty for safe persistence
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "default", ignore = true)
    DeliveryAddressEntity toEntity(DeliveryAddressRequest request);
}