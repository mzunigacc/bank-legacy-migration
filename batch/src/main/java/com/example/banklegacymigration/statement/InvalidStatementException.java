package com.example.banklegacymigration.statement;

public class InvalidStatementException extends RuntimeException {

    public InvalidStatementException(String message) {
        super(message);
    }
}