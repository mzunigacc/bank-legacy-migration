package com.example.paymentservice.dto;

import java.math.BigDecimal;

public record CreditResult(
        Long cuentaId,
        BigDecimal monto,
        BigDecimal saldoAnterior,
        BigDecimal saldoNuevo
) {
}
