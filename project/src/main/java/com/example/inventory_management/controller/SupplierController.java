package com.example.inventory_management.controller;

import com.example.inventory_management.entity.Supplier;
import com.example.inventory_management.service.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // Display all suppliers
    @GetMapping
    public String getAllSuppliers(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "suppliers";
    }

    // Display supplier details
    @GetMapping("/{id}")
    public String getSupplierById(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.getSupplierById(id));
        return "supplier-details";
    }

    // Create supplier
    @PostMapping
    public String createSupplier(@ModelAttribute Supplier supplier) {
        supplierService.createSupplier(supplier);
        return "redirect:/suppliers";
    }

    // Update supplier
    @PostMapping("/update/{id}")
    public String updateSupplier(@PathVariable Long id,
                                 @ModelAttribute Supplier supplier) {

        supplierService.updateSupplier(id, supplier);

        return "redirect:/suppliers";
    }

    // Delete supplier
    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Long id) {

        supplierService.deleteSupplier(id);

        return "redirect:/suppliers";
    }
}