package com.example.inventory_management.service.impl;

import java.util.List;

import com.example.inventory_management.entity.Supplier;
import com.example.inventory_management.exception.BusinessRuleException;
import com.example.inventory_management.exception.ResourceNotFoundException;
import com.example.inventory_management.repository.StockTransactionRepository;
import com.example.inventory_management.repository.SupplierRepository;
import com.example.inventory_management.service.SupplierService;

import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository,
                               StockTransactionRepository stockTransactionRepository) {
        this.supplierRepository = supplierRepository;
        this.stockTransactionRepository = stockTransactionRepository;
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier supplier) {

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        existingSupplier.setName(supplier.getName());
        existingSupplier.setContactEmail(supplier.getContactEmail());
        existingSupplier.setPhone(supplier.getPhone());

        return supplierRepository.save(existingSupplier);
    }

    @Override
    public Supplier patchSupplier(Long id, String name, String contactEmail, String phone) {

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        if (name != null) {
            existingSupplier.setName(name);
        }
        if (contactEmail != null) {
            existingSupplier.setContactEmail(contactEmail);
        }
        if (phone != null) {
            existingSupplier.setPhone(phone);
        }

        return supplierRepository.save(existingSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {

        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }

        // Business rule: a supplier cannot be removed while stock transactions still reference it
        boolean inUse = !stockTransactionRepository.findAll().stream()
                .filter(t -> t.getSupplier() != null && t.getSupplier().getSupplierId().equals(id))
                .toList().isEmpty();

        if (inUse) {
            throw new BusinessRuleException(
                    "Cannot delete supplier with id " + id + " because it is referenced by existing stock transactions");
        }

        supplierRepository.deleteById(id);
    }
}
