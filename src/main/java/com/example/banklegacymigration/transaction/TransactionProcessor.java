package com.example.banklegacymigration.transaction;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransactionProcessor
        implements ItemProcessor<Transaction, Transaction> {

    @Override
    public Transaction process(Transaction transaction) {

        if (transaction.getMonto().compareTo(BigDecimal.ZERO) < 0) {
            transaction.setAnomalia(true);
            transaction.setMotivo("Monto negativo");
        } else if (transaction.getMonto().compareTo(BigDecimal.ZERO) == 0) {
            transaction.setAnomalia(true);
            transaction.setMotivo("Monto igual a cero");
        }

        return transaction;
    }
}