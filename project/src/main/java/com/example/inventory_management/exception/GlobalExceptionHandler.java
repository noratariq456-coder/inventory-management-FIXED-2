package com.example.inventory_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - entity does not exist
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(ResourceNotFoundException ex) {
        return baseError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409 - request conflicts with a domain business rule (duplicate SKU, insufficient stock, etc.)
    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleBusinessRule(BusinessRuleException ex) {
        return baseError(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 401 - bad credentials on /api/auth/login
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleAuthenticationException(AuthenticationException ex) {
        return baseError(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    // 403 - authenticated but not authorized
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleAccessDenied(AccessDeniedException ex) {
        return baseError(HttpStatus.FORBIDDEN, "You do not have permission to perform this action");
    }

    // 400 - bean validation failures (@Valid on request bodies)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        Map<String, Object> error = baseError(HttpStatus.BAD_REQUEST, "Validation failed");
        error.put("fieldErrors", fieldErrors);
        return error;
    }

    // 400 - fallback for any other unexpected runtime error surfaced from the service layer
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleRuntimeException(RuntimeException ex) {
        return baseError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private Map<String, Object> baseError(HttpStatus status, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("error", status.getReasonPhrase());
        error.put("message", message);
        return error;
    }
}
