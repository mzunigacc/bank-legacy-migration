package com.example.bankcore.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Fondos insuficientes");
    }
}