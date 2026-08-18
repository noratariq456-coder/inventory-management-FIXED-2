package com.example.inventory_management.controller;

import com.example.inventory_management.dto.SupplierPatchRequest;
import com.example.inventory_management.entity.Supplier;
import com.example.inventory_management.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierRestController {

    private final SupplierService supplierService;

    public SupplierRestController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    // All suppliers come from MySQL via SupplierRepository -> SupplierService, no mock data.
    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public Supplier getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id);
    }

    @PostMapping
    public Supplier createSupplier(@Valid @RequestBody Supplier supplier) {
        return supplierService.createSupplier(supplier);
    }

    @PutMapping("/{id}")
    public Supplier updateSupplier(@PathVariable Long id, @Valid @RequestBody Supplier supplier) {
        return supplierService.updateSupplier(id, supplier);
    }

    @PatchMapping("/{id}")
    public Supplier patchSupplier(@PathVariable Long id, @RequestBody SupplierPatchRequest patch) {
        return supplierService.patchSupplier(id, patch.getName(), patch.getContactEmail(), patch.getPhone());
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
    }
}
