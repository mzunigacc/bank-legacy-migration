package com.example.bankcore.exception;

public class InvalidWithdrawalAmountException extends RuntimeException {

    public InvalidWithdrawalAmountException() {
        super("El monto debe ser mayor a cero");
    }
}