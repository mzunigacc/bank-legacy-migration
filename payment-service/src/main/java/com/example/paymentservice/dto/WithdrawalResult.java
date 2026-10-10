package com.example.paymentservice.dto;

import java.math.BigDecimal;

public record WithdrawalResult(
        Long withdrawalId,
        Long cuentaId,
        BigDecimal monto,
        BigDecimal saldoAnterior,
        BigDecimal saldoNuevo
) {
}
