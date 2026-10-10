package com.example.paymentservice.exception;

public class AccountServiceUnavailableException extends RuntimeException {

    public AccountServiceUnavailableException() {
        super("Account Service no se encuentra disponible temporalmente");
    }

    public AccountServiceUnavailableException(Throwable cause) {
        super(
                "Account Service no se encuentra disponible temporalmente",
                cause
        );
    }
}
