package com.example.banklegacymigration.interest;

public class InvalidInterestAccountException extends RuntimeException {

    public InvalidInterestAccountException(String message) {
        super(message);
    }
}