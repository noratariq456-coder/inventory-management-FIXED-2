package com.example.inventory_management.service;

import com.example.inventory_management.dto.ProductPatchRequest;
import com.example.inventory_management.entity.Product;
import com.example.inventory_management.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    List<Product> getAllProducts();

    // Sorting (legacy single-field helper, kept for backward compatibility)
    List<Product> getAllProductsSorted(String field);

    // Main list API: pagination + sorting (via Pageable) + keyword search + category/price filters
    Page<Product> getProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Product getProductById(Long id);

    Product updateProduct(Long id, Product product);

    Product patchProduct(Long id, ProductPatchRequest patch);

    void deleteProduct(Long id);

    // Search
    List<Product> searchProducts(String keyword);

    // Summary
    Long getTotalProducts();

    // ربط مورد بمنتج
    Product addSupplierToProduct(Long productId, Long supplierId);

    // فك ربط مورد عن منتج
    Product removeSupplierFromProduct(Long productId, Long supplierId);

    // تنفيذ انتقال حالة الـ workflow (مع فرض القواعد المسموحة)
    Product changeStatus(Long productId, ProductStatus newStatus);
}