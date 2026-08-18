package com.example.inventory_management.repository;

import com.example.inventory_management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findBySkuContainingIgnoreCase(String keyword);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);
}