package com.example.paymentservice.exception;

public class InvalidTransferException extends RuntimeException {

    public InvalidTransferException() {
        super("La transferencia solicitada no es válida");
    }
}
