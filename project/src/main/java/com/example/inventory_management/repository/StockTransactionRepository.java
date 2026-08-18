package com.example.inventory_management.repository;

import com.example.inventory_management.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long>, JpaSpecificationExecutor<StockTransaction> {

    List<StockTransaction> findByProductProductId(Long productId);

    // Real aggregate queries executed by MySQL (SUM / AVG), not computed in Java.
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM StockTransaction t WHERE t.transactionType = 'STOCK_IN'")
    long sumStockIn();

    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM StockTransaction t WHERE t.transactionType = 'STOCK_OUT'")
    long sumStockOut();

    @Query("SELECT AVG(t.quantity) FROM StockTransaction t")
    BigDecimal averageQuantity();
}
