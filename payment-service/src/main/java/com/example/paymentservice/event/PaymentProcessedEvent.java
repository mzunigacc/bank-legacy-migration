package com.example.paymentservice.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentProcessedEvent(
        Long operationId,
        String operationType,
        Long sourceAccountId,
        Long targetAccountId,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
