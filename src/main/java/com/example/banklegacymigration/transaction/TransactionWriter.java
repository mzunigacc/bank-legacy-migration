package com.example.banklegacymigration.transaction;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class TransactionWriter
        implements ItemWriter<Transaction> {

    @Override
    public void write(Chunk<? extends Transaction> transactions) {

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }
}