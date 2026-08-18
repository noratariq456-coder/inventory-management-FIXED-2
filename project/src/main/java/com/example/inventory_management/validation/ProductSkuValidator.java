package com.example.inventory_management.validation;

import com.example.inventory_management.entity.Product;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

/**
 * Custom business validation that can't be expressed with a single bean-validation
 * annotation: SKU must be uppercase alphanumeric with optional dashes, 3-20 chars.
 * Registered per-controller via @InitBinder, runs alongside @Valid / JSR-303.
 */
public class ProductSkuValidator implements Validator {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z0-9-]{3,20}$");

    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Product product = (Product) target;

        if (product.getSku() != null && !SKU_PATTERN.matcher(product.getSku()).matches()) {
            errors.rejectValue("sku", "sku.invalidFormat",
                    "SKU must be 3-20 uppercase letters, digits, or dashes (e.g. SKU-1001)");
        }
    }
}
