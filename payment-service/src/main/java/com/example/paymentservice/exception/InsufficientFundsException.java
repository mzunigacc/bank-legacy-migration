package com.example.paymentservice.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Fondos insuficientes");
    }
}
