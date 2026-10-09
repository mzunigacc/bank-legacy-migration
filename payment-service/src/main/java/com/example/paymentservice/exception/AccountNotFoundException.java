package com.example.paymentservice.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(Long cuentaId) {
        super("Cuenta no encontrada: " + cuentaId);
    }
}
