package com.example.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotNull(message = "El identificador de cuenta es obligatorio")
        @Positive(message = "El identificador de cuenta debe ser mayor a cero")
        Long cuentaId,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 0, message = "La edad no puede ser negativa")
        Integer edad,

        @NotBlank(message = "El tipo de cuenta es obligatorio")
        String tipo,

        @NotNull(message = "El saldo inicial es obligatorio")
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "El saldo inicial no puede ser negativo"
        )
        BigDecimal saldoInicial
) {
}
