package com.example.paymentservice.dto;

import java.math.BigDecimal;

public record AccountTransferRequest(
        Long cuentaDestinoId,
        BigDecimal monto
) {
}
