package com.pim.service;

import com.pim.model.dto.CategoryDTO;
import com.pim.model.entity.Category;
import com.pim.model.entity.CategoryNameTranslation;
import com.pim.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceTest.class);

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private UUID categoryId;

    @BeforeEach
    public void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category();
        category.setId(categoryId);

        CategoryNameTranslation translation = new CategoryNameTranslation();
        translation.setLanguageCode("en");
        translation.setNameTranslation("Test Category");
        translation.setCategory(category);

        category.setNameTranslations(List.of(translation));
        logger.info("Set up test data with categoryId: {}", categoryId);
    }

    @Test
    public void testGetAllCategories() {
        logger.info("Starting testGetAllCategories");
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        List<Category> categories = categoryService.getAllCategories();
        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals(category, categories.get(0));
        logger.info("Finished testGetAllCategories");
    }

    @Test
    public void testGetCategoryById() {
        logger.info("Starting testGetCategoryById");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Category foundCategory = categoryService.getCategoryById(categoryId);
        assertNotNull(foundCategory);
        assertEquals(category, foundCategory);
        logger.info("Finished testGetCategoryById");
    }

    @Test
    public void testGetCategoryById_NotFound() {
        logger.info("Starting testGetCategoryById_NotFound");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () ->
                categoryService.getCategoryById(categoryId)
        );
        assertEquals("Category not found", exception.getMessage());
        logger.info("Finished testGetCategoryById_NotFound");
    }

    @Test
    public void testAddCategories() {
        logger.info("Starting testAddCategories");

        Map<String, Map<String, String>> categoriesMap = Map.of(
                "Test Category", Map.of("en", "Test Category")
        );
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategories(categoriesMap);

        when(categoryRepository.saveAll(anyList())).thenReturn(List.of(category));

        List<Category> createdCategories = categoryService.addCategories(categoryDTO);

        assertNotNull(createdCategories);
        assertEquals(1, createdCategories.size());
        assertEquals("Test Category", createdCategories.get(0).getNameTranslations().get(0).getNameTranslation());

        logger.info("Finished testAddCategories");
    }

    @Test
    public void testUpdateCategory() {
        logger.info("Starting testUpdateCategory");
        String updatedName = "Updated Category";

        Map<String, Map<String, String>> categoriesMap = Map.of(
                "Updated Category", Map.of("en", updatedName)
        );
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategories(categoriesMap);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.updateCategory(categoryId, categoryDTO);

        assertNotNull(result);
        assertEquals(updatedName, result.getNameTranslations().get(0).getNameTranslation());

        verify(categoryRepository).findById(categoryId);
        assertEquals(updatedName, category.getNameTranslations().get(0).getNameTranslation());

        logger.info("Finished testUpdateCategory");
    }

    @Test
    public void testDeleteCategory() {
        logger.info("Starting testDeleteCategory");
        doNothing().when(categoryRepository).deleteById(categoryId);
        categoryService.deleteCategory(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
        logger.info("Finished testDeleteCategory");
    }

    @Test
    public void testDeleteAllCategories() {
        logger.info("Starting testDeleteAllCategories");
        doNothing().when(categoryRepository).deleteAllInBatch();
        when(categoryRepository.count()).thenReturn(1L);
        categoryService.deleteAllCategories();
        verify(categoryRepository, times(1)).deleteAllInBatch();
        logger.info("Finished testDeleteAllCategories");
    }

    @Test
    public void testCountCategories() {
        logger.info("Starting testCountCategories");
        when(categoryRepository.count()).thenReturn(1L);
        Long count = categoryService.countCategories();
        assertEquals(1L, count);
        logger.info("Finished testCountCategories");
    }
}