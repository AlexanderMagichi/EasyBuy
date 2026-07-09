package com.teamchallenge.easybuy.product.mapper.category;

import com.teamchallenge.easybuy.product.dto.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.product.entity.category.AttributeType;
import com.teamchallenge.easybuy.product.entity.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryAttributeMapper {

    CategoryAttributeMapper INSTANCE = Mappers.getMapper(CategoryAttributeMapper.class);

    @Mapping(source = "type", target = "type", qualifiedByName = "mapTypeToString")
    @Mapping(source = "category.id", target = "categoryId")
    CategoryAttributeDTO toDto(CategoryAttribute attribute);

    @Mapping(source = "type", target = "type", qualifiedByName = "mapStringToType")
    @Mapping(source = "categoryId", target = "category.id")
    CategoryAttribute toEntity(CategoryAttributeDTO categoryAttributeDTO);

    @Named("mapTypeToString")
    default String mapTypeToString(AttributeType type) {
        return type != null ? type.name() : null;
    }

    @Named("mapStringToType")
    default AttributeType mapStringToType(String type) {
        if (type == null) {
            return null;
        }
        try {
            return AttributeType.valueOf(type);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid attribute type: " + type, ex);
        }
    }
}