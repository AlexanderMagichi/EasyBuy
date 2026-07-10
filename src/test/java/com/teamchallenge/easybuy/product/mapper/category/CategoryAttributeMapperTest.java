package com.teamchallenge.easybuy.product.mapper.category;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.product.entity.category.AttributeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoryAttributeMapperTest {

    @Test
    void mapStringToType_returnsEnumForValidValue() {
        assertEquals(AttributeType.STRING, CategoryAttributeMapper.INSTANCE.mapStringToType("STRING"));
    }

    @Test
    void mapStringToType_returnsNullForNullValue() {
        assertNull(CategoryAttributeMapper.INSTANCE.mapStringToType(null));
    }

    @Test
    void mapStringToType_throwsForInvalidValue() {
        assertThrows(IllegalArgumentException.class,
                () -> CategoryAttributeMapper.INSTANCE.mapStringToType("INVALID_TYPE"));
    }
}

