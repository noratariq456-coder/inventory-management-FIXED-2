package com.example.inventory_management.dto;

import java.math.BigDecimal;

/**
 * All fields optional/nullable - only non-null fields are applied.
 * Used for PATCH /api/products/{id} (partial update, as opposed to PUT which is a full replace).
 */
public class ProductPatchRequest {

    private String name;
    private BigDecimal price;
    private Long categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
