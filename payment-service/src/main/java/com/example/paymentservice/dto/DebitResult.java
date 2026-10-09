package com.example.paymentservice.dto;

import java.math.BigDecimal;

public record DebitResult(
        Long cuentaId,
        BigDecimal monto,
        BigDecimal saldoAnterior,
        BigDecimal saldoNuevo
) {
}
