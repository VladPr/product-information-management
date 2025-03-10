package com.pim.service;

import com.pim.model.Category;
import com.pim.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        category.setName(Map.of("en", "Test Category"));
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
    public void testCreateCategory() {
        logger.info("Starting testCreateCategory");
        when(categoryRepository.save(category)).thenReturn(category);
        Category createdCategory = categoryService.createCategory(category);
        assertNotNull(createdCategory);
        assertEquals(category, createdCategory);
        logger.info("Finished testCreateCategory");
    }

    @Test
    public void testUpdateCategory() {
        logger.info("Starting testUpdateCategory");
        String updatedName = "Updated Category";
        Category updatedCategory = new Category();
        updatedCategory.setName(Map.of("en", updatedName));

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoryService.updateCategory(categoryId, updatedCategory);

        assertNotNull(result);
        assertEquals(updatedName, result.getName().get("en"));

        verify(categoryRepository).findById(categoryId);
        assertEquals(updatedName, category.getName().get("en"));

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
}