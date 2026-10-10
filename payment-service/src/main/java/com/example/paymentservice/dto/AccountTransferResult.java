package com.example.paymentservice.dto;

import java.math.BigDecimal;

public record AccountTransferResult(
        Long cuentaOrigenId,
        Long cuentaDestinoId,
        BigDecimal monto,
        BigDecimal saldoOrigenAnterior,
        BigDecimal saldoOrigenNuevo,
        BigDecimal saldoDestinoAnterior,
        BigDecimal saldoDestinoNuevo
) {
}
