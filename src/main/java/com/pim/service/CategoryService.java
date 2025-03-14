package com.pim.service;

import com.pim.model.dto.CategoryDTO;
import com.pim.model.entity.Category;
import com.pim.model.entity.CategoryNameTranslation;
import com.pim.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        logger.info("Fetching all categories");
        return categoryRepository.findAll();
    }

    public Category getCategoryById(UUID id) {
        logger.info("Fetching category with id: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public List<Category> addCategories(CategoryDTO categoryDTO) {
        logger.info("Creating new categories");

        if (categoryDTO.getCategories() == null || categoryDTO.getCategories().isEmpty()) {
            logger.error("The request body is empty.");
            throw new IllegalArgumentException("Category data cannot be empty");
        }

        List<Category> categories = categoryDTO.getCategories().entrySet().stream().map(entry -> {
            Category category = new Category();
            category.setName(entry.getKey()); // Set the name of the category
            List<CategoryNameTranslation> translations = entry.getValue().entrySet().stream()
                    .map(langEntry -> new CategoryNameTranslation(langEntry.getKey(), langEntry.getValue(), category))
                    .toList();
            category.setNameTranslations(translations);
            return category;
        }).toList();

        return categoryRepository.saveAll(categories);
    }

    @Transactional
    public Category updateCategory(UUID id, CategoryDTO categoryDTO) {
        logger.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (categoryDTO.getCategories() == null || categoryDTO.getCategories().isEmpty()) {
            logger.error("The request body is empty.");
            throw new IllegalArgumentException("Category data cannot be empty");
        }

        String newName = categoryDTO.getCategories().keySet().iterator().next();
        Map<String, String> newTranslations = categoryDTO.getCategories().get(newName);

        boolean isNameChanged = !category.getName().equals(newName);
        boolean areTranslationsChanged = !category.getNameTranslations().stream()
                .collect(Collectors.toMap(CategoryNameTranslation::getLanguageCode, CategoryNameTranslation::getNameTranslation))
                .equals(newTranslations);

        if (!isNameChanged && !areTranslationsChanged) {
            logger.info("No changes detected for category with id: {}", id);
            return category;
        }

        if (isNameChanged) {
            category.setName(newName);
        }

        if (areTranslationsChanged) {
            category.getNameTranslations().clear();
            category.setNameTranslations(newTranslations.entrySet().stream()
                    .map(entry -> new CategoryNameTranslation(entry.getKey(), entry.getValue(), category))
                    .toList());
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        logger.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllCategories() {
        logger.warn("Deleting all categories from the database.");
        long categoryCount = categoryRepository.count();

        if (categoryCount == 0) {
            logger.warn("No categories found to delete.");
            return;
        }

        try {
            categoryRepository.deleteAllInBatch();
            logger.info("Successfully deleted all categories from the database.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting all categories: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete all categories", e);
        }
    }

    public Long countCategories() {
        logger.info("Counting all categories");
        return categoryRepository.count();
    }

    public CategoryDTO convertCategoryToDTO(List<Category> categories) {
        Map<String, Map<String, String>> categoryMap = categories.stream()
                .collect(Collectors.toMap(
                        category -> category.getNameTranslations().stream()
                                .filter(translation -> "en".equals(translation.getLanguageCode())) // Default to English name
                                .findFirst()
                                .map(CategoryNameTranslation::getNameTranslation)
                                .orElse(UUID.randomUUID().toString()), // Fallback if no English name exists
                        category -> category.getNameTranslations().stream()
                                .collect(Collectors.toMap(
                                        CategoryNameTranslation::getLanguageCode,
                                        CategoryNameTranslation::getNameTranslation
                                ))
                ));

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategories(categoryMap);
        return categoryDTO;
    }
}