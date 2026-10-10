package com.example.bffweb.dto;

public record WebCustomerResponse(
        Long cuentaId,
        String nombre,
        Integer edad
) {
}
