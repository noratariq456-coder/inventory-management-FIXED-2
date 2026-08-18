package com.example.inventory_management.service;

import com.example.inventory_management.entity.Supplier;
import java.util.List;

public interface SupplierService {

    Supplier createSupplier(Supplier supplier);

    List<Supplier> getAllSuppliers();

    Supplier getSupplierById(Long id);

    Supplier updateSupplier(Long id, Supplier supplier);

    Supplier patchSupplier(Long id, String name, String contactEmail, String phone);

    void deleteSupplier(Long id);

}