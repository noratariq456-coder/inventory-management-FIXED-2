package com.example.inventory_management.controller;

import com.example.inventory_management.entity.Product;
import com.example.inventory_management.service.CategoryService;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.StockTransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final StockTransactionService stockTransactionService;

    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             StockTransactionService stockTransactionService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.stockTransactionService = stockTransactionService;
    }

    // Display all products
    @GetMapping
    public String getAllProducts(Model model) {

        List<Product> products = productService.getAllProducts();

        model.addAttribute("products", products);
        model.addAttribute("quantities", buildQuantityMap(products));
        model.addAttribute("suppliersByProduct", buildSuppliersMap(products));

        return "products";
    }

    // Search products
    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model) {

        List<Product> products = productService.searchProducts(keyword);

        model.addAttribute("products", products);
        model.addAttribute("quantities", buildQuantityMap(products));
        model.addAttribute("suppliersByProduct", buildSuppliersMap(products));

        return "products";
    }

    // Sort products
    @GetMapping("/sort")
    public String sortProducts(@RequestParam String field, Model model) {

        List<Product> products = productService.getAllProductsSorted(field);

        model.addAttribute("products", products);
        model.addAttribute("quantities", buildQuantityMap(products));
        model.addAttribute("suppliersByProduct", buildSuppliersMap(products));

        return "products";
    }

    // Show Add Product form
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "add-product";
    }

    // Save new product
    @PostMapping
    public String createProduct(@ModelAttribute Product product) {
        productService.createProduct(product);
        return "redirect:/products";
    }

    // Show Product Details
    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("currentStock", stockTransactionService.getCurrentStock(id));
        model.addAttribute("suppliers", stockTransactionService.getSuppliersForProduct(id));
        return "product-details";
    }

    // Show Edit Product form
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {

        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());

        return "edit-product";
    }

    // Update Product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product) {

        productService.updateProduct(id, product);

        return "redirect:/products";
    }

    // Delete Product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return "redirect:/products";
    }

    private Map<Long, Integer> buildQuantityMap(List<Product> products) {

        Map<Long, Integer> quantities = new HashMap<>();

        for (Product product : products) {
            quantities.put(product.getProductId(),
                    stockTransactionService.getCurrentStock(product.getProductId()));
        }

        return quantities;
    }

    private Map<Long, String> buildSuppliersMap(List<Product> products) {

        Map<Long, String> suppliersByProduct = new HashMap<>();

        for (Product product : products) {

            String names = stockTransactionService.getSuppliersForProduct(product.getProductId())
                    .stream()
                    .map(supplier -> supplier.getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            suppliersByProduct.put(product.getProductId(), names);
        }

        return suppliersByProduct;
    }
}
