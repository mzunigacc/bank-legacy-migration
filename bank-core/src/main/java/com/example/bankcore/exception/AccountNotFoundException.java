package com.example.bankcore.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long cuentaId) {
        super("No existe la cuenta " + cuentaId);
    }
}