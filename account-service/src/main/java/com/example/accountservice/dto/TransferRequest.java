package com.example.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "La cuenta destino es obligatoria")
        @Positive(message = "La cuenta destino debe ser mayor a cero")
        Long cuentaDestinoId,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal monto
) {
}
