package com.teamchallenge.easybuy.product.service.image;

import com.teamchallenge.easybuy.infrastructure.image.CloudinaryImageService;
import com.teamchallenge.easybuy.product.dto.GoodsImageDTO;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.entity.GoodsImage;
import com.teamchallenge.easybuy.product.exception.GoodsImageException;
import com.teamchallenge.easybuy.product.mapper.GoodsImageMapper;
import com.teamchallenge.easybuy.product.repository.GoodsImageRepository;
import com.teamchallenge.easybuy.product.repository.GoodsImageSpecifications;
import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for handling image operations related to goods.
 * Includes Cloudinary integration, persistence, and caching.
 */
@Service
@Tag(name = "Goods Image Service", description = "Business logic for uploading, updating, retrieving and deleting goods images")
@RequiredArgsConstructor
public class GoodsImageService {

    private final GoodsImageRepository goodsImageRepository;
    private final GoodsImageMapper goodsImageMapper;
    private final CloudinaryImageService cloudinaryImageService;
    private final GoodsRepository goodsRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "'all'")
    @Operation(summary = "Get all goods images", description = "Returns all images stored for all goods.")
    public List<GoodsImageDTO> getAllImages() {
        return goodsImageRepository.findAll().stream()
                .map(goodsImageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "#id")
    @Operation(summary = "Get image by ID", description = "Returns a single image by its unique ID.")
    public GoodsImageDTO getImageById(UUID id) {
        GoodsImage image = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));
        return goodsImageMapper.toDto(image);
    }

    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    @Operation(summary = "Create image", description = "Uploads image to Cloudinary and saves reference in DB.")
    public GoodsImageDTO createImage(UUID goodsId, MultipartFile file) throws IOException {
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new GoodsImageException("Goods with ID " + goodsId + " not found for image creation"));

        String imageUrl = cloudinaryImageService.uploadImage(file,
                String.format("easybuy/shops/%s/goods/%s", goods.getShop().getShopId(), goods.getArt()));

        GoodsImage newImage = GoodsImage.builder()
                .imageUrl(imageUrl)
                .goods(goods)
                .build();

        return goodsImageMapper.toDto(goodsImageRepository.save(newImage));
    }

    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    @Operation(summary = "Update image", description = "Replaces existing image in Cloudinary and updates DB reference.")
    public GoodsImageDTO updateImage(UUID id, UUID goodsId, MultipartFile file) throws IOException {
        GoodsImage existingImage = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));

        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new GoodsImageException("Goods with ID " + goodsId + " not found for image update"));

        String oldPublicId = cloudinaryImageService.extractPublicIdFromUrl(existingImage.getImageUrl());
        if (oldPublicId != null) {
            cloudinaryImageService.deleteImage(oldPublicId);
        }

        String newImageUrl = cloudinaryImageService.uploadImage(file,
                String.format("easybuy/shops/%s/goods/%s", goods.getShop().getShopId(), goods.getArt()));

        existingImage.setImageUrl(newImageUrl);
        return goodsImageMapper.toDto(goodsImageRepository.save(existingImage));
    }

    @Transactional
    @CacheEvict(value = "goodsImages", allEntries = true)
    @Operation(summary = "Delete image", description = "Deletes image from Cloudinary and removes reference from DB.")
    public void deleteImage(UUID id) throws IOException {
        GoodsImage image = goodsImageRepository.findById(id)
                .orElseThrow(() -> new GoodsImageException("Image with ID " + id + " not found"));

        String publicId = cloudinaryImageService.extractPublicIdFromUrl(image.getImageUrl());
        if (publicId != null) {
            cloudinaryImageService.deleteImage(publicId);
        }

        goodsImageRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "goodsImages", key = "#goodsId")
    @Operation(summary = "Search images by goods ID", description = "Finds all images attached to a specific goods item.")
    public List<GoodsImageDTO> searchImages(UUID goodsId) {
        Specification<GoodsImage> spec = Specification.where(GoodsImageSpecifications.hasGoodsId(goodsId));
        return goodsImageRepository.findAll(spec).stream()
                .map(goodsImageMapper::toDto)
                .collect(Collectors.toList());
    }
}