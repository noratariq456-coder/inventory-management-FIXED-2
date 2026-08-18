package com.example.inventory_management.controller;

import com.example.inventory_management.entity.Category;
import com.example.inventory_management.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public Category createCategory(@Valid @RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id,
                                   @Valid @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

    // Partial update - only overwrite fields present in the request body
    @PatchMapping("/{id}")
    public Category patchCategory(@PathVariable Long id, @RequestBody Category patch) {
        Category existing = categoryService.getCategoryById(id);

        if (patch.getName() != null) {
            existing.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            existing.setDescription(patch.getDescription());
        }

        return categoryService.updateCategory(id, existing);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}

