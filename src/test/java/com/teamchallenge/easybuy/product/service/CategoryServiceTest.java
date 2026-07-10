package com.teamchallenge.easybuy.product.service;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import com.teamchallenge.easybuy.product.dto.category.CategoryDTO;
import com.teamchallenge.easybuy.product.exception.CategoryNotFoundException;
import com.teamchallenge.easybuy.product.mapper.category.CategoryMapper;
import com.teamchallenge.easybuy.product.entity.category.Category;
import com.teamchallenge.easybuy.product.repository.category.CategoryRepository;
import com.teamchallenge.easybuy.product.service.category.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");
        category.setParentCategory(null);
        category.setSubcategories(new ArrayList<>());
        category.setAttributes(new ArrayList<>());

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryId);
        categoryDTO.setName("Electronics");
        categoryDTO.setParentId(null);
        categoryDTO.setSubcategoryIds(new ArrayList<>());
        categoryDTO.setAttributes(new ArrayList<>());
    }

    @Test
    @DisplayName("getAllCategories should return a list of category DTOs")
    void getAllCategories_ShouldReturnListOfCategories() {
        List<Category> categories = Collections.singletonList(category);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(categoryDTO);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(expectedDTOs, result);
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getAllCategories should return an empty list when no categories exist")
    void getAllCategories_ShouldReturnEmptyList_WhenNoCategoriesExist() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertTrue(result.isEmpty());
        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    @DisplayName("getCategoryById should return category DTO when found")
    void getCategoryById_ShouldReturnCategory_WhenFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.getCategoryById(categoryId);

        assertEquals(categoryDTO, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getCategoryById should throw CategoryNotFoundException when not found")
    void getCategoryById_ShouldThrowException_WhenNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, never()).toDto(any(Category.class));
    }

    @Test
    @DisplayName("createCategory should create and return a new category DTO")
    void createCategory_ShouldCreateAndReturnCategory() {

        when(categoryMapper.toEntity(any(CategoryDTO.class))).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDTO);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertEquals(categoryDTO, result);
        verify(categoryMapper, times(1)).toEntity(categoryDTO);
        verify(categoryRepository, times(1)).save(category);
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("updateCategory should update and return the updated category DTO")
    void updateCategory_ShouldUpdateAndReturnCategory() {
        CategoryDTO updatedDTO = new CategoryDTO();
        updatedDTO.setId(categoryId);
        updatedDTO.setName("Updated Electronics");

        Category updatedCategoryEntity = new Category();
        updatedCategoryEntity.setId(categoryId);
        updatedCategoryEntity.setName("Updated Electronics");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toEntity(any(CategoryDTO.class))).thenReturn(updatedCategoryEntity);
        when(categoryRepository.save(updatedCategoryEntity)).thenReturn(updatedCategoryEntity);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(updatedDTO);

        CategoryDTO result = categoryService.updateCategory(categoryId, updatedDTO);

        assertEquals(updatedDTO, result);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(updatedCategoryEntity);
    }

    @Test
    @DisplayName("updateCategory should throw CategoryNotFoundException when category to update is not found")
    void updateCategory_ShouldThrowException_WhenNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(categoryId, categoryDTO));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryMapper, never()).toEntity(any(CategoryDTO.class));
    }

    @Test
    @DisplayName("deleteCategory should delete category when found")
    void deleteCategory_ShouldDeleteCategory_WhenFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("deleteCategory should throw CategoryNotFoundException when category to delete is not found")
    void deleteCategory_ShouldThrowException_WhenNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("getAllRootCategories should return a list of root category DTOs")
    void getAllRootCategories_ShouldReturnRootCategories() {
        List<Category> rootCategories = Collections.singletonList(category);
        List<CategoryDTO> expectedDTOs = Collections.singletonList(categoryDTO);

        when(categoryRepository.findAllRootCategories()).thenReturn(rootCategories);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDTO);

        List<CategoryDTO> result = categoryService.getAllRootCategories();

        assertEquals(expectedDTOs, result);
        verify(categoryRepository, times(1)).findAllRootCategories();
        verify(categoryMapper, times(1)).toDto(category);
    }

    @Test
    @DisplayName("getAllSubcategoriesByParentId should return a list of subcategory DTOs")
    void getAllSubcategoriesByParentId_ShouldReturnSubcategories() {
        Category subcategory = new Category();
        subcategory.setId(UUID.randomUUID());
        subcategory.setName("Laptops");

        CategoryDTO subcategoryDTO = new CategoryDTO();
        subcategoryDTO.setId(subcategory.getId());
        subcategoryDTO.setName("Laptops");

        when(categoryRepository.findAllSubcategoriesByParentId(categoryId)).thenReturn(Collections.singletonList(subcategory));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(subcategoryDTO);

        List<CategoryDTO> result = categoryService.getAllSubcategoriesByParentId(categoryId);

        assertFalse(result.isEmpty());
        assertEquals(subcategoryDTO.getName(), result.get(0).getName());
        verify(categoryRepository, times(1)).findAllSubcategoriesByParentId(categoryId);
    }
}