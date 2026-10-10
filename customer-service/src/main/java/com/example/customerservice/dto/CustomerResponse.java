package com.example.customerservice.dto;

public record CustomerResponse(
        Long cuentaId,
        String nombre,
        Integer edad
) {
}
