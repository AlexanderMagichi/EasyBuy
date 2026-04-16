package com.teamchallenge.easybuy.product.service.category;

import com.teamchallenge.easybuy.product.dto.category.GoodsAttributeValueDTO;
import com.teamchallenge.easybuy.product.exception.GoodsAttributeValueException;
import com.teamchallenge.easybuy.product.mapper.category.GoodsAttributeValueMapper;
import com.teamchallenge.easybuy.product.entity.category.GoodsAttributeValue;
import com.teamchallenge.easybuy.product.repository.GoodsAttributeValueRepository;
import com.teamchallenge.easybuy.product.repository.category.GoodsAttributeValueSpecifications;
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
 * Provides business operations for GoodsAttributeValueService.
 */
@Service
public class GoodsAttributeValueService {

    private final GoodsAttributeValueRepository goodsAttributeValueRepository;
    private final GoodsAttributeValueMapper goodsAttributeValueMapper;

    @Autowired
    public GoodsAttributeValueService(GoodsAttributeValueRepository goodsAttributeValueRepository, GoodsAttributeValueMapper goodsAttributeValueMapper) {
        this.goodsAttributeValueRepository = goodsAttributeValueRepository;
        this.goodsAttributeValueMapper = goodsAttributeValueMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "attributeValues", key = "'all'")
    public List<GoodsAttributeValueDTO> getAllAttributeValues() {
        return goodsAttributeValueRepository.findAll().stream()
                .map(goodsAttributeValueMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "attributeValues", key = "#id")
    public GoodsAttributeValueDTO getAttributeValueById(UUID id) {
        GoodsAttributeValue value = goodsAttributeValueRepository.findById(id)
                .orElseThrow(() -> new GoodsAttributeValueException("Attribute value with ID " + id + " not found"));
        return goodsAttributeValueMapper.toDto(value);
    }

    @Transactional
    @CacheEvict(value = "attributeValues", allEntries = true)
    public GoodsAttributeValueDTO createAttributeValue(GoodsAttributeValueDTO dto) {
        GoodsAttributeValue value = goodsAttributeValueMapper.toEntity(dto);
        return goodsAttributeValueMapper.toDto(goodsAttributeValueRepository.save(value));
    }

    @Transactional
    @CacheEvict(value = "attributeValues", allEntries = true)
    public GoodsAttributeValueDTO updateAttributeValue(UUID id, GoodsAttributeValueDTO dto) {
        GoodsAttributeValue existingValue = goodsAttributeValueRepository.findById(id)
                .orElseThrow(() -> new GoodsAttributeValueException("Attribute value with ID " + id + " not found"));
        GoodsAttributeValue updatedValue = goodsAttributeValueMapper.toEntity(dto);
        updatedValue.setId(id);
        return goodsAttributeValueMapper.toDto(goodsAttributeValueRepository.save(updatedValue));
    }

    @Transactional
    @CacheEvict(value = "attributeValues", allEntries = true)
    public void deleteAttributeValue(UUID id) {
        GoodsAttributeValue value = goodsAttributeValueRepository.findById(id)
                .orElseThrow(() -> new GoodsAttributeValueException("Attribute value with ID " + id + " not found"));
        goodsAttributeValueRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "attributeValues", key = "{#goodsId}")
    public List<GoodsAttributeValueDTO> searchAttributeValues(UUID goodsId) {
        Specification<GoodsAttributeValue> spec = Specification.where(GoodsAttributeValueSpecifications.hasGoodsId(goodsId));
        return goodsAttributeValueRepository.findAll(spec).stream()
                .map(goodsAttributeValueMapper::toDto)
                .collect(Collectors.toList());
    }
}
