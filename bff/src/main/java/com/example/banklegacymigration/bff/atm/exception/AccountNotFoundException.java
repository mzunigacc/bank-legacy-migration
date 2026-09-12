package com.example.banklegacymigration.bff.atm.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long cuentaId) {
        super("Cuenta no encontrada: " + cuentaId);
    }
}