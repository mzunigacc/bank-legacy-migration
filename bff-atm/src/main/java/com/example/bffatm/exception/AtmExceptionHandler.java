package com.example.bffatm.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AtmExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Solicitud inválida");

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", message
                ));
    }

    @ExceptionHandler(CoreApiException.class)
    public ResponseEntity<Map<String, String>> handleCoreApi(
            CoreApiException exception) {

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(Map.of(
                        "error", exception.getMessage()
                ));
    }
}