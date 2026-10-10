package com.example.bffweb.client.dto;

public record CustomerServiceResponse(
        Long cuentaId,
        String nombre,
        Integer edad
) {
}
