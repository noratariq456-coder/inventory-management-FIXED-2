package com.example.inventory_management.exception;

/**
 * Thrown when a request violates a domain business rule
 * (e.g. duplicate SKU, insufficient stock, deleting a referenced record).
 * Mapped to HTTP 409 CONFLICT by {@link GlobalExceptionHandler}.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
