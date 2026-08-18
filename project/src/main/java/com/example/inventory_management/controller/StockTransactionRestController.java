package com.example.inventory_management.controller;

import com.example.inventory_management.dto.PageResponse;
import com.example.inventory_management.dto.StockDashboardResponse;
import com.example.inventory_management.entity.StockTransaction;
import com.example.inventory_management.service.StockTransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock-transactions")
public class StockTransactionRestController {

    private final StockTransactionService stockTransactionService;

    public StockTransactionRestController(StockTransactionService stockTransactionService) {
        this.stockTransactionService = stockTransactionService;
    }

    // Main list: pagination + sorting + filters (productId, supplierId, transactionType)
    // Example: /api/stock-transactions?page=0&size=10&sort=transactionDate,desc&productId=1&transactionType=STOCK_IN
    @GetMapping
    public PageResponse<StockTransaction> getAllStockTransactions(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String transactionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate,desc") String sort) {

        String[] sortParts = sort.split(",");
        Sort.Direction direction = (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        return PageResponse.from(
                stockTransactionService.getTransactions(productId, supplierId, transactionType, pageable));
    }

    // Get transaction by ID
    @GetMapping("/{id}")
    public StockTransaction getTransactionById(@PathVariable Long id) {
        return stockTransactionService.getTransactionById(id);
    }

    // Create transaction (enforces product/supplier existence + insufficient-stock business rule)
    @PostMapping
    public StockTransaction createTransaction(@Valid @RequestBody StockTransaction transaction) {
        return stockTransactionService.createTransaction(transaction);
    }

    // Update transaction (full replace)
    @PutMapping("/{id}")
    public StockTransaction updateTransaction(@PathVariable Long id,
                                              @Valid @RequestBody StockTransaction transaction) {
        return stockTransactionService.updateTransaction(id, transaction);
    }

    // Partial update - quantity or transaction type only
    @PatchMapping("/{id}")
    public StockTransaction patchTransaction(@PathVariable Long id, @RequestBody StockTransaction transaction) {
        StockTransaction existing = stockTransactionService.getTransactionById(id);

        if (transaction.getQuantity() != null) {
            existing.setQuantity(transaction.getQuantity());
        }
        if (transaction.getTransactionType() != null) {
            existing.setTransactionType(transaction.getTransactionType());
        }

        return stockTransactionService.updateTransaction(id, existing);
    }

    // Delete transaction
    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id) {
        stockTransactionService.deleteTransaction(id);
    }

    // Dashboard: real SUM/COUNT/AVG aggregates from MySQL
    @GetMapping("/dashboard")
    public StockDashboardResponse getDashboard() {
        return stockTransactionService.getDashboardSummary();
    }
}
