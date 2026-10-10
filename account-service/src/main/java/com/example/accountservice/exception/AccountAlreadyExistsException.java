package com.example.accountservice.exception;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(Long cuentaId) {
        super("La cuenta " + cuentaId + " ya existe");
    }
}
