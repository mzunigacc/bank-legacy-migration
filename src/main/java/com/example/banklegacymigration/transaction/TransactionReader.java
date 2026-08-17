package com.example.banklegacymigration.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class TransactionReader {

    @Bean
    public FlatFileItemReader<Transaction> transactionItemReader() {

        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .resource(new FileSystemResource("data/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .fieldSetMapper(fieldSet -> new Transaction(
                        fieldSet.readLong("id"),
                        LocalDate.parse(fieldSet.readString("fecha")),
                        new BigDecimal(fieldSet.readString("monto")),
                        fieldSet.readString("tipo")
                ))
                .build();
    }
}