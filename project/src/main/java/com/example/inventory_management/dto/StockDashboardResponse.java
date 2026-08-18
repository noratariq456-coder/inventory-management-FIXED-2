package com.example.inventory_management.dto;

import java.math.BigDecimal;

public class StockDashboardResponse {

    private long totalStockIn;
    private long totalStockOut;
    private long netStock;
    private long totalTransactions;
    private BigDecimal averageTransactionQuantity;

    public StockDashboardResponse() {
    }

    public StockDashboardResponse(long totalStockIn, long totalStockOut, long totalTransactions,
                                   BigDecimal averageTransactionQuantity) {
        this.totalStockIn = totalStockIn;
        this.totalStockOut = totalStockOut;
        this.netStock = totalStockIn - totalStockOut;
        this.totalTransactions = totalTransactions;
        this.averageTransactionQuantity = averageTransactionQuantity;
    }

    public long getTotalStockIn() {
        return totalStockIn;
    }

    public void setTotalStockIn(long totalStockIn) {
        this.totalStockIn = totalStockIn;
    }

    public long getTotalStockOut() {
        return totalStockOut;
    }

    public void setTotalStockOut(long totalStockOut) {
        this.totalStockOut = totalStockOut;
    }

    public long getNetStock() {
        return netStock;
    }

    public void setNetStock(long netStock) {
        this.netStock = netStock;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public BigDecimal getAverageTransactionQuantity() {
        return averageTransactionQuantity;
    }

    public void setAverageTransactionQuantity(BigDecimal averageTransactionQuantity) {
        this.averageTransactionQuantity = averageTransactionQuantity;
    }
}
