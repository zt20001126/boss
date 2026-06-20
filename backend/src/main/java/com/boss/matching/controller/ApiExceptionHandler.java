package com.boss.matching.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Converts validation, business, and unexpected exceptions into the existing API error response shape.
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    /**
     * Handles validation.
     * @param ex input value
     * @return result value
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<Map<String, String>> validation(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("message", "请求参数不完整或格式不正确"));
    }

    /**
     * Handles business.
     * @param ex input value
     * @return result value
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> business(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    /**
     * Handles internal.
     * @param ex input value
     * @return result value
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> internal(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of("message", "服务暂时不可用，请稍后重试"));
    }
}
