package com.example.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResult(
        Long operationId,
        String operationType,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
