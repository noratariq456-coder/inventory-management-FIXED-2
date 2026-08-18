package com.example.inventory_management.service.impl;

import java.util.List;

import com.example.inventory_management.entity.Category;
import com.example.inventory_management.exception.BusinessRuleException;
import com.example.inventory_management.exception.ResourceNotFoundException;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.CategoryRepository;
import com.example.inventory_management.service.CategoryService;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("categoryServiceImpl")
@Primary
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public Category updateCategory(Long id, Category category) {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {

        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        // Business rule: a category cannot be removed while products still reference it
        boolean inUse = productRepository.findAll().stream()
                .anyMatch(product -> product.getCategory() != null
                        && product.getCategory().getCategoryId().equals(id));

        if (inUse) {
            throw new BusinessRuleException(
                    "Cannot delete category with id " + id + " because it is still assigned to one or more products");
        }

        categoryRepository.deleteById(id);
    }
}