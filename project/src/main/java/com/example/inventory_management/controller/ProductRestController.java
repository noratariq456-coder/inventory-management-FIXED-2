package com.example.inventory_management.controller;

import com.example.inventory_management.dto.PageResponse;
import com.example.inventory_management.dto.ProductPatchRequest;
import com.example.inventory_management.entity.Product;
import com.example.inventory_management.entity.ProductStatus;
import com.example.inventory_management.entity.Supplier;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.StockTransactionService;
import com.example.inventory_management.validation.ProductSkuValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;
    private final StockTransactionService stockTransactionService;

    public ProductRestController(ProductService productService,
                                 StockTransactionService stockTransactionService) {
        this.productService = productService;
        this.stockTransactionService = stockTransactionService;
    }

    // Runs the custom SKU-format validator in addition to the entity's @Valid/JSR-303 annotations
    // whenever the request body being bound is a Product.
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() instanceof Product) {
            binder.addValidators(new ProductSkuValidator());
        }
    }

    // Main list endpoint: pagination + sorting + keyword search + 2 filters (category, price range).
    // Example: /api/products?page=0&size=10&sort=name,asc&keyword=lap&categoryId=1&minPrice=10&maxPrice=500
    @GetMapping
    public PageResponse<Product> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId,asc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        return PageResponse.from(
                productService.getProducts(keyword, categoryId, minPrice, maxPrice, pageable));
    }

    // Get product by ID
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    // Create product
    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        return productService.createProduct(product);
    }

    // Update product (full replace)
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @Valid @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    // Partial update (name / price / category only)
    @PatchMapping("/{id}")
    public Product patchProduct(@PathVariable Long id, @RequestBody ProductPatchRequest patch) {
        return productService.patchProduct(id, patch);
    }

    // Current stock quantity (محسوبة من حركات المخزون)
    @GetMapping("/{id}/quantity")
    public Integer getCurrentQuantity(@PathVariable Long id) {
        return stockTransactionService.getCurrentStock(id);
    }

    // الموردون المرتبطون بالمنتج (مستخرجون من حركات المخزون)
    @GetMapping("/{id}/suppliers")
    public List<Supplier> getSuppliers(@PathVariable Long id) {
        return stockTransactionService.getSuppliersForProduct(id);
    }

    // ربط مورد بمنتج (يدعم أكثر من مورد للمنتج الواحد)
    @PostMapping("/{id}/suppliers/{supplierId}")
    public Product addSupplier(@PathVariable Long id, @PathVariable Long supplierId) {
        return productService.addSupplierToProduct(id, supplierId);
    }

    // فك ربط مورد عن منتج
    @DeleteMapping("/{id}/suppliers/{supplierId}")
    public Product removeSupplier(@PathVariable Long id, @PathVariable Long supplierId) {
        return productService.removeSupplierFromProduct(id, supplierId);
    }

    // الموردون المرتبطون مباشرة بالمنتج (غير الـ /suppliers اللي فوق، هذا رابط مباشر)
    @GetMapping("/{id}/linked-suppliers")
    public List<Supplier> getLinkedSuppliers(@PathVariable Long id) {
        return List.copyOf(productService.getProductById(id).getSuppliers());
    }

    // Custom finder / free-text search (kept as a dedicated endpoint in addition to the main list's keyword filter)
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    // Summary aggregate (COUNT)
    @GetMapping("/summary")
    public Long getTotalProducts() {
        return productService.getTotalProducts();
    }



    // انتقال حالة الـ workflow. مثال للجسم المُرسل: { "status": "DISCONTINUED" }
    // القاعدة (الانتقالات المسموحة) مفروضة في الـ service؛ الانتقال غير المسموح يرجع 409.
    @PutMapping("/{id}/status")
    public Product changeStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        ProductStatus newStatus = ProductStatus.valueOf(body.get("status"));
        return productService.changeStatus(id, newStatus);
    }

    // Delete product
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}