package com.example.customerservice.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long cuentaId) {
        super("Cliente no encontrado para la cuenta: " + cuentaId);
    }
}
