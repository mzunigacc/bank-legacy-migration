package com.example.accountservice.dto;

import java.math.BigDecimal;

public record TransferResult(
        Long cuentaOrigenId,
        Long cuentaDestinoId,
        BigDecimal monto,
        BigDecimal saldoOrigenAnterior,
        BigDecimal saldoOrigenNuevo,
        BigDecimal saldoDestinoAnterior,
        BigDecimal saldoDestinoNuevo
) {
}
