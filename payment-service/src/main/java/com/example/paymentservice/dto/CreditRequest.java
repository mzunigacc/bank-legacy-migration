package com.example.paymentservice.dto;

import java.math.BigDecimal;

public record CreditRequest(
        BigDecimal monto
) {
}
