package com.pim.controller;

import com.pim.model.dto.CategoryDTO;
import com.pim.model.entity.Category;
import com.pim.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping("/add")
    public List<Category> addCategories(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.addCategories(categoryDTO);
    }

    @PostMapping("/update/{id}")
    public Category updateCategories(@PathVariable UUID id, @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(id, categoryDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllCategories() {
        categoryService.deleteAllCategories();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public Long countCategories() {
        return categoryService.countCategories();
    }
}