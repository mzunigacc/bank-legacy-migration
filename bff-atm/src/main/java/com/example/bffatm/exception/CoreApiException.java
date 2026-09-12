package com.example.bffatm.exception;

import org.springframework.http.HttpStatusCode;

public class CoreApiException extends RuntimeException {

    private final HttpStatusCode statusCode;

    public CoreApiException(
            HttpStatusCode statusCode,
            String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}