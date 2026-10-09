package com.example.paymentservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WithdrawalCreatedEvent(
        Long withdrawalId,
        Long cuentaId,
        BigDecimal monto,
        BigDecimal saldoAnterior,
        BigDecimal saldoNuevo,
        LocalDateTime fechaHora
) {
}
