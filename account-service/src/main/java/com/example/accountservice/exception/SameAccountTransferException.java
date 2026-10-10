package com.example.accountservice.exception;

public class SameAccountTransferException extends RuntimeException {

    public SameAccountTransferException() {
        super("La cuenta origen y destino deben ser diferentes");
    }
}
