package com.example.banklegacymigration.transaction;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransactionProcessor
        implements ItemProcessor<Transaction, Transaction> {

    @Override
    public Transaction process(Transaction transaction) {

        return transaction;
    }
}