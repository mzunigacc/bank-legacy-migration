package com.example.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferPaymentRequest(

        @NotNull(message = "La cuenta origen es obligatoria")
        @Positive(message = "La cuenta origen debe ser mayor a cero")
        Long cuentaOrigenId,

        @NotNull(message = "La cuenta destino es obligatoria")
        @Positive(message = "La cuenta destino debe ser mayor a cero")
        Long cuentaDestinoId,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor a cero")
        BigDecimal monto
) {
}
