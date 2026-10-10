package com.example.paymentservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Solicitud inválida");

        return ResponseEntity.badRequest().body(
                Map.of(
                        "error", "VALIDATION_ERROR",
                        "message", message
                )
        );
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAccountNotFound(
            AccountNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "error", "ACCOUNT_NOT_FOUND",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientFunds(
            InsufficientFundsException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "error", "INSUFFICIENT_FUNDS",
                        "message", ex.getMessage()
                )
        );
    }

    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleAccountServiceUnavailable(
            AccountServiceUnavailableException ex
    ) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                Map.of(
                        "error", "ACCOUNT_SERVICE_UNAVAILABLE",
                        "message", ex.getMessage()
                )
        );
    }

}
