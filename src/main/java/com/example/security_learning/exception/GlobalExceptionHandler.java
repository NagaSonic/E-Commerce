package com.example.security_learning.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles application-level exceptions globally and returns
 * consistent JSON responses for REST APIs.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid request bodies.
     */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, Object>> handleBusinessException(
	        BusinessException exception) {

	    Map<String, Object> response = new HashMap<>();
	    response.put("error", "Business rule violation");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(response);
	}
    
    

    /**
     * Handles expected business-rule errors.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException exception) {

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Business rule violation");
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}