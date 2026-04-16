package com.teamchallenge.easybuy.product.service.category;


import com.teamchallenge.easybuy.product.dto.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.product.exception.CategoryAttributeException;
import com.teamchallenge.easybuy.product.mapper.category.CategoryAttributeMapper;
import com.teamchallenge.easybuy.product.entity.category.AttributeType;
import com.teamchallenge.easybuy.product.entity.category.CategoryAttribute;
import com.teamchallenge.easybuy.product.repository.category.CategoryAttributeRepository;
import com.teamchallenge.easybuy.product.repository.category.CategoryAttributeSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Provides business operations for CategoryAttributeService.
 */
@Service
public class CategoryAttributeService {

    private final CategoryAttributeRepository categoryAttributeRepository;
    private final CategoryAttributeMapper categoryAttributeMapper;

    @Autowired
    public CategoryAttributeService(CategoryAttributeRepository categoryAttributeRepository, CategoryAttributeMapper categoryAttributeMapper) {
        this.categoryAttributeRepository = categoryAttributeRepository;
        this.categoryAttributeMapper = categoryAttributeMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryAttributes", key = "'all'")
    public List<CategoryAttributeDTO> getAllAttributes() {
        return categoryAttributeRepository.findAll().stream()
                .map(categoryAttributeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryAttributes", key = "#id")
    public CategoryAttributeDTO getAttributeById(UUID id) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new CategoryAttributeException("Attribute with ID " + id + " not found"));
        return categoryAttributeMapper.toDto(attribute);
    }

    @Transactional
    @CacheEvict(value = "categoryAttributes", allEntries = true)
    public CategoryAttributeDTO createAttribute(CategoryAttributeDTO dto) {
        CategoryAttribute attribute = categoryAttributeMapper.toEntity(dto);
        return categoryAttributeMapper.toDto(categoryAttributeRepository.save(attribute));
    }

    @Transactional
    @CacheEvict(value = "categoryAttributes", allEntries = true)
    public CategoryAttributeDTO updateAttribute(UUID id, CategoryAttributeDTO dto) {
        CategoryAttribute existingAttribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new CategoryAttributeException("Attribute with ID " + id + " not found"));
        CategoryAttribute updatedAttribute = categoryAttributeMapper.toEntity(dto);
        updatedAttribute.setId(id);
        return categoryAttributeMapper.toDto(categoryAttributeRepository.save(updatedAttribute));
    }

    @Transactional
    @CacheEvict(value = "categoryAttributes", allEntries = true)
    public void deleteAttribute(UUID id) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new CategoryAttributeException("Attribute with ID " + id + " not found"));
        categoryAttributeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoryAttributes", key = "{#name, #categoryId, #type}")
    public List<CategoryAttributeDTO> searchAttributes(String name, UUID categoryId, AttributeType type) {
        Specification<CategoryAttribute> spec = Specification.where(CategoryAttributeSpecifications.hasName(name))
                .and(CategoryAttributeSpecifications.hasCategoryId(categoryId))
                .and(CategoryAttributeSpecifications.hasType(type));
        return categoryAttributeRepository.findAll(spec).stream()
                .map(categoryAttributeMapper::toDto)
                .collect(Collectors.toList());
    }
}
