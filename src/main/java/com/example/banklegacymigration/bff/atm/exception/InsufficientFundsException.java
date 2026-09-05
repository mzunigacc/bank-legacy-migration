package com.example.banklegacymigration.bff.atm.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Saldo insuficiente");
    }
}