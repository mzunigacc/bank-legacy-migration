package com.example.paymentservice.dto;

import java.math.BigDecimal;

public record DebitRequest(BigDecimal monto) {
}
