package com.freelance.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * 
 * Handles exceptions across the entire application
 * and returns appropriate HTTP responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            log.error("Field error - {}: {}", fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle malformed JSON or unreadable request body
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Malformed request body: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        String message = ex.getMessage();
        if (message != null && message.contains("JSON")) {
            error.put("error", "Invalid JSON format in request body");
        } else {
            error.put("error", "Invalid request body format");
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle IllegalArgumentException (e.g., invalid role)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle RuntimeException (e.g., Keycloak errors)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException occurred: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        // 409 Conflict: username or email already exists in Keycloak
        if (message != null && (message.contains("409") || message.contains("User exists with same username") || message.contains("User exists with same email"))) {
            message = "Username or email already exists. Please use different credentials.";
            status = HttpStatus.CONFLICT;
        } else if (message != null && message.contains("Keycloak") && (message.contains("404") || message.contains("admin access token"))) {
            // Keep explicit message from KeycloakService when another app is on port 8080
            if (!message.contains("Another application") && !message.contains("Port 8080 returned 404 with an HTML")) {
                if (message.contains("register") || message.contains("create user") || message.contains("realms/")) {
                    message = "Realm 'projetpidev' not found in Keycloak. Create it in Keycloak Admin Console (see docs/KEYCLOAK_SETUP.md).";
                } else {
                    message = "Keycloak is unavailable. Ensure Keycloak is running on port 8080 and no other application is using that port.";
                }
            }
        } else if (message != null && message.contains("Could not find role")) {
            message = "Registration configuration error (realm role missing). Please try again or contact support.";
        } else if (message != null && message.contains(":")) {
            message = message.substring(message.lastIndexOf(":") + 1).trim();
            if (message.length() > 200 || message.contains("<") || message.contains("DOCTYPE")) {
                message = "Registration service temporarily unavailable. Please try again later.";
            }
        }
        error.put("error", message != null ? message : "An error occurred during registration");
        return new ResponseEntity<>(error, status);
    }
    
    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + (ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName()));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
