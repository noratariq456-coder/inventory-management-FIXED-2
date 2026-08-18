package com.example.inventory_management.controller;

import com.example.inventory_management.entity.StockTransaction;
import com.example.inventory_management.service.ProductService;
import com.example.inventory_management.service.StockTransactionService;
import com.example.inventory_management.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class StockTransactionController {

    private final StockTransactionService stockTransactionService;
    private final ProductService productService;
    private final SupplierService supplierService;

    public StockTransactionController(StockTransactionService stockTransactionService,
                                      ProductService productService,
                                      SupplierService supplierService) {
        this.stockTransactionService = stockTransactionService;
        this.productService = productService;
        this.supplierService = supplierService;
    }

    // Display all transactions
    @GetMapping
    public String getAllTransactions(Model model) {
        model.addAttribute("transactions", stockTransactionService.getAllTransactions());
        return "transactions";
    }

    // Display transaction details
    @GetMapping("/{id}")
    public String getTransactionById(@PathVariable Long id, Model model) {
        model.addAttribute("transaction", stockTransactionService.getTransactionById(id));
        return "transaction-details";
    }

    // Show Add Transaction form
    @GetMapping("/add")
    public String showAddTransactionForm(Model model) {
        model.addAttribute("transaction", new StockTransaction());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "add-transaction";
    }

    // Create transaction
    @PostMapping
    public String createTransaction(@ModelAttribute StockTransaction transaction) {
        stockTransactionService.createTransaction(transaction);
        return "redirect:/transactions";
    }

    // Update transaction
    @PostMapping("/update/{id}")
    public String updateTransaction(@PathVariable Long id,
                                    @ModelAttribute StockTransaction transaction) {

        stockTransactionService.updateTransaction(id, transaction);

        return "redirect:/transactions";
    }

    // Delete transaction
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {

        stockTransactionService.deleteTransaction(id);

        return "redirect:/transactions";
    }
}
