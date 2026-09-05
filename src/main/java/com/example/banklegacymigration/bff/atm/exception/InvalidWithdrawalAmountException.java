package com.example.banklegacymigration.bff.atm.exception;

public class InvalidWithdrawalAmountException extends RuntimeException {

    public InvalidWithdrawalAmountException() {
        super("El monto del retiro debe ser mayor que cero");
    }
}