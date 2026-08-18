package com.example.inventory_management.service;

import com.example.inventory_management.dto.StockDashboardResponse;
import com.example.inventory_management.entity.StockTransaction;
import com.example.inventory_management.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockTransactionService {

    StockTransaction createTransaction(StockTransaction transaction);

    List<StockTransaction> getAllTransactions();

    // Main list: pagination + sorting + filters (productId, supplierId, transactionType)
    Page<StockTransaction> getTransactions(Long productId, Long supplierId, String transactionType, Pageable pageable);

    StockTransaction getTransactionById(Long id);

    StockTransaction updateTransaction(Long id, StockTransaction transaction);

    void deleteTransaction(Long id);

    // الكمية الحالية للمنتج، محسوبة من كل حركات المخزون (بدل تخزينها داخل المنتج)
    Integer getCurrentStock(Long productId);

    // الموردين المرتبطين بمنتج معيّن، مستخرَجين من حركات المخزون (لا يوجد ربط مباشر بين المنتج والمورد)
    List<Supplier> getSuppliersForProduct(Long productId);

    // Dashboard: real SUM/COUNT/AVG aggregates computed by MySQL
    StockDashboardResponse getDashboardSummary();
}
