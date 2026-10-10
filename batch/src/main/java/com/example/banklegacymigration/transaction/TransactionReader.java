package com.example.banklegacymigration.transaction;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class TransactionReader {

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> transactionItemReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        return new FlatFileItemReaderBuilder<Transaction>()
                .name("transactionItemReader")
                .resource(new FileSystemResource(
                        "../data/semana3/movimientos_financieros_diarios.csv"
                ))
                .linesToSkip(1)
                .delimited()
                .names("id", "fecha", "monto", "tipo")
                .fieldSetMapper(fieldSet -> new Transaction(
                        fieldSet.readLong("id"),
                        fieldSet.readString("fecha"),
                        fieldSet.readString("monto"),
                        fieldSet.readString("tipo")
                ))
                .currentItemCount(start)
                .maxItemCount(end)
                .build();
    }
}
