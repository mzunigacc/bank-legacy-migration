package com.example.accountservice.dto;

import java.math.BigDecimal;

public record DebitResult(
        Long cuentaId,
        BigDecimal monto,
        BigDecimal saldoAnterior,
        BigDecimal saldoNuevo
) {
}
