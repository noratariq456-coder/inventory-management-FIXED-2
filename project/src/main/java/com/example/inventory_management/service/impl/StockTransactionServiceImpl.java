package com.example.inventory_management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.inventory_management.dto.StockDashboardResponse;
import com.example.inventory_management.entity.Product;
import com.example.inventory_management.entity.StockTransaction;
import com.example.inventory_management.entity.Supplier;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.StockTransactionRepository;
import com.example.inventory_management.repository.SupplierRepository;
import com.example.inventory_management.service.StockTransactionService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class StockTransactionServiceImpl implements StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public StockTransactionServiceImpl(
            StockTransactionRepository stockTransactionRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository) {

        this.stockTransactionRepository = stockTransactionRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public StockTransaction createTransaction(StockTransaction transaction) {

        if (transaction.getProduct() == null || transaction.getProduct().getProductId() == null) {
            throw new com.example.inventory_management.exception.BusinessRuleException("Product is required");
        }

        if (transaction.getSupplier() == null || transaction.getSupplier().getSupplierId() == null) {
            throw new com.example.inventory_management.exception.BusinessRuleException("Supplier is required");
        }

        Product product = productRepository.findById(transaction.getProduct().getProductId())
                .orElseThrow(() -> new com.example.inventory_management.exception.ResourceNotFoundException("Product not found"));

        Supplier supplier = supplierRepository.findById(transaction.getSupplier().getSupplierId())
                .orElseThrow(() -> new com.example.inventory_management.exception.ResourceNotFoundException("Supplier not found"));

        transaction.setProduct(product);
        transaction.setSupplier(supplier);

        if (transaction.getQuantity() == null || transaction.getQuantity() <= 0) {
            throw new com.example.inventory_management.exception.BusinessRuleException("Transaction quantity must be greater than zero");
        }

        if ("STOCK_OUT".equalsIgnoreCase(transaction.getTransactionType())) {

            int currentStock = getCurrentStock(product.getProductId());

            if (currentStock < transaction.getQuantity()) {
                throw new com.example.inventory_management.exception.BusinessRuleException("Insufficient stock for STOCK_OUT: requested " + transaction.getQuantity() + " but only " + getCurrentStock(product.getProductId()) + " available");
            }
        }

        if (transaction.getTransactionDate() == null) {
            transaction.setTransactionDate(LocalDateTime.now());
        }

        return stockTransactionRepository.save(transaction);
    }

    @Override
    public List<StockTransaction> getAllTransactions() {
        return stockTransactionRepository.findAll();
    }

    @Override
    public Page<StockTransaction> getTransactions(Long productId, Long supplierId, String transactionType, Pageable pageable) {

        Specification<StockTransaction> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (productId != null) {
                predicates.add(cb.equal(root.get("product").get("productId"), productId));
            }

            if (supplierId != null) {
                predicates.add(cb.equal(root.get("supplier").get("supplierId"), supplierId));
            }

            if (transactionType != null && !transactionType.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("transactionType")), transactionType.toUpperCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return stockTransactionRepository.findAll(spec, pageable);
    }

    @Override
    public StockTransaction getTransactionById(Long id) {
        return stockTransactionRepository.findById(id)
                .orElseThrow(() -> new com.example.inventory_management.exception.ResourceNotFoundException("Transaction not found"));
    }

    @Override
    public StockTransaction updateTransaction(Long id, StockTransaction transaction) {

        StockTransaction existingTransaction = stockTransactionRepository.findById(id)
                .orElseThrow(() -> new com.example.inventory_management.exception.ResourceNotFoundException("Transaction not found"));

        Product product = productRepository.findById(
                transaction.getProduct().getProductId())
                .orElseThrow(() -> new com.example.inventory_management.exception.ResourceNotFoundException("Product not found"));

        Supplier supplier = supplierRepository.findById(
                transaction.getSupplier().getSupplierId())
                .orElseThrow(() -> new com.example.inventory_management.exception.ResourceNotFoundException("Supplier not found"));

        existingTransaction.setTransactionType(transaction.getTransactionType());
        existingTransaction.setQuantity(transaction.getQuantity());
        existingTransaction.setTransactionDate(transaction.getTransactionDate());
        existingTransaction.setProduct(product);
        existingTransaction.setSupplier(supplier);

        return stockTransactionRepository.save(existingTransaction);
    }

    @Override
    public void deleteTransaction(Long id) {

        if (!stockTransactionRepository.existsById(id)) {
            throw new com.example.inventory_management.exception.ResourceNotFoundException("Transaction not found");
        }

        stockTransactionRepository.deleteById(id);
    }

    @Override
    public Integer getCurrentStock(Long productId) {

        List<StockTransaction> transactions = stockTransactionRepository.findByProductProductId(productId);

        int stock = 0;

        for (StockTransaction transaction : transactions) {

            if ("STOCK_IN".equalsIgnoreCase(transaction.getTransactionType())) {
                stock += transaction.getQuantity();
            } else if ("STOCK_OUT".equalsIgnoreCase(transaction.getTransactionType())) {
                stock -= transaction.getQuantity();
            }
        }

        return stock;
    }

    @Override
    public List<Supplier> getSuppliersForProduct(Long productId) {

        List<StockTransaction> transactions = stockTransactionRepository.findByProductProductId(productId);

        // نستخدم LinkedHashMap عشان نحذف التكرار ونحافظ على ترتيب أول ظهور
        Map<Long, Supplier> suppliers = new LinkedHashMap<>();

        for (StockTransaction transaction : transactions) {
            Supplier supplier = transaction.getSupplier();
            if (supplier != null) {
                suppliers.put(supplier.getSupplierId(), supplier);
            }
        }

        return new ArrayList<>(suppliers.values());
    }

    @Override
    public StockDashboardResponse getDashboardSummary() {

        long stockIn = stockTransactionRepository.sumStockIn();
        long stockOut = stockTransactionRepository.sumStockOut();
        long count = stockTransactionRepository.count();
        BigDecimal average = stockTransactionRepository.averageQuantity();

        return new StockDashboardResponse(stockIn, stockOut, count,
                average != null ? average : BigDecimal.ZERO);
    }
}
