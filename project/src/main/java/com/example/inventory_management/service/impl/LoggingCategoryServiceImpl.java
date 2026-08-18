package com.example.inventory_management.service.impl;

import com.example.inventory_management.entity.Category;
import com.example.inventory_management.service.CategoryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Second implementation of {@link CategoryService}: a logging decorator around
 * the primary, repository-backed {@link CategoryServiceImpl}. Not wired as the
 * default (see @Primary on CategoryServiceImpl) - inject with
 * @Qualifier("loggingCategoryService") wherever audit logging of category
 * operations is needed.
 */
@Service("loggingCategoryService")
public class LoggingCategoryServiceImpl implements CategoryService {

    private static final Logger LOGGER = Logger.getLogger(LoggingCategoryServiceImpl.class.getName());

    private final CategoryService delegate;

    public LoggingCategoryServiceImpl(@Qualifier("categoryServiceImpl") CategoryService delegate) {
        this.delegate = delegate;
    }

    @Override
    public Category createCategory(Category category) {
        LOGGER.log(Level.INFO, "Creating category: {0}", category.getName());
        return delegate.createCategory(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return delegate.getAllCategories();
    }

    @Override
    public Category getCategoryById(Long id) {
        return delegate.getCategoryById(id);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        LOGGER.log(Level.INFO, "Updating category id: {0}", id);
        return delegate.updateCategory(id, category);
    }

    @Override
    public void deleteCategory(Long id) {
        LOGGER.log(Level.INFO, "Deleting category id: {0}", id);
        delegate.deleteCategory(id);
    }
}
