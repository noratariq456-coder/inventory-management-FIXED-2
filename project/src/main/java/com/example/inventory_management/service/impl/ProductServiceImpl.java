package com.example.inventory_management.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.inventory_management.dto.ProductPatchRequest;
import com.example.inventory_management.entity.Category;
import com.example.inventory_management.entity.Product;
import com.example.inventory_management.entity.ProductStatus;
import com.example.inventory_management.entity.Supplier;
import com.example.inventory_management.exception.BusinessRuleException;
import com.example.inventory_management.exception.ResourceNotFoundException;
import com.example.inventory_management.repository.CategoryRepository;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.SupplierRepository;
import com.example.inventory_management.service.ProductService;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              SupplierRepository supplierRepository,
                              CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(Product product) {

        if (productRepository.existsBySku(product.getSku())) {
            throw new BusinessRuleException("SKU already exists: " + product.getSku());
        }

        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Price cannot be negative");
        }

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllProductsSorted(String field) {
        return productRepository.findAll(Sort.by(field));
    }

    @Override
    public Page<Product> getProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        Specification<Product> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("sku")), like)
                ));
            }

            // Filter 1: category
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            }

            // Filter 2: price range
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public Product updateProduct(Long id, Product product) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Product skuProduct = productRepository.findBySku(product.getSku()).orElse(null);

        if (skuProduct != null && !skuProduct.getProductId().equals(id)) {
            throw new BusinessRuleException("SKU already exists: " + product.getSku());
        }

        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Price cannot be negative");
        }

        existingProduct.setName(product.getName());
        existingProduct.setSku(product.getSku());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());

        return productRepository.save(existingProduct);
    }

    @Override
    public Product patchProduct(Long id, ProductPatchRequest patch) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (patch.getName() != null) {
            existingProduct.setName(patch.getName());
        }

        if (patch.getPrice() != null) {
            if (patch.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessRuleException("Price cannot be negative");
            }
            existingProduct.setPrice(patch.getPrice());
        }

        if (patch.getCategoryId() != null) {
            Category category = categoryRepository.findById(patch.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + patch.getCategoryId()));
            existingProduct.setCategory(category);
        }

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchProducts(String keyword) {

        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);

        if (!products.isEmpty()) {
            return products;
        }

        return productRepository.findBySkuContainingIgnoreCase(keyword);
    }

    @Override
    public Long getTotalProducts() {
        return productRepository.count();
    }

    @Override
    public Product addSupplierToProduct(Long productId, Long supplierId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + supplierId));

        if (!product.getSuppliers().contains(supplier)) {
            product.getSuppliers().add(supplier);
        }

        return productRepository.save(product);
    }

    @Override
    public Product removeSupplierFromProduct(Long productId, Long supplierId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }

        product.getSuppliers().removeIf(s -> s.getSupplierId().equals(supplierId));

        return productRepository.save(product);
    }

    @Override
    public Product changeStatus(Long productId, ProductStatus newStatus) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        ProductStatus current = product.getStatus();

        // ما فيه فايدة من انتقال لنفس الحالة
        if (current == newStatus) {
            throw new BusinessRuleException("Product is already " + current);
        }

        // قاعدة العمل: نتحقق إن الانتقال مسموح من الحالة الحالية
        if (!isTransitionAllowed(current, newStatus)) {
            throw new BusinessRuleException(
                    "Invalid status transition: " + current + " -> " + newStatus);
        }

        product.setStatus(newStatus);
        return productRepository.save(product);
    }

    /**
     * الانتقالات المسموحة في دورة حياة المنتج:
     *   ACTIVE       -> DISCONTINUED
     *   DISCONTINUED -> ACTIVE أو ARCHIVED
     *   ARCHIVED     -> (حالة نهائية، ما فيه رجوع)
     */
    private boolean isTransitionAllowed(ProductStatus from, ProductStatus to) {
        return switch (from) {
            case ACTIVE       -> to == ProductStatus.DISCONTINUED;
            case DISCONTINUED -> to == ProductStatus.ACTIVE || to == ProductStatus.ARCHIVED;
            case ARCHIVED     -> false; // نهائية
        };
    }
}