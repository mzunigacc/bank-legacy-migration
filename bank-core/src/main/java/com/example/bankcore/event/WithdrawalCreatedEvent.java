package com.example.bankcore.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WithdrawalCreatedEvent(
        Long withdrawalId,
        Long accountId,
        BigDecimal amount,
        BigDecimal previousBalance,
        BigDecimal newBalance,
        LocalDateTime occurredAt
) {
}