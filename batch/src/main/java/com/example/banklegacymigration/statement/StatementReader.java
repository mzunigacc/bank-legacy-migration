package com.example.banklegacymigration.statement;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class StatementReader {

    @Bean
    @StepScope
    public FlatFileItemReader<AnnualStatement> statementItemReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        return new FlatFileItemReaderBuilder<AnnualStatement>()
                .name("statementItemReader")
                .resource(
                        new FileSystemResource(
                                "../data/semana3/estados_financieros_anuales.csv"
                        )
                )
                .linesToSkip(1)
                .delimited()
                .names(
                        "cuenta_id",
                        "fecha",
                        "transaccion",
                        "monto",
                        "descripcion"
                )
                .fieldSetMapper(fieldSet -> new AnnualStatement(
                        fieldSet.readLong("cuenta_id"),
                        fieldSet.readString("fecha"),
                        fieldSet.readString("transaccion"),
                        fieldSet.readString("monto"),
                        fieldSet.readString("descripcion")
                ))
                .currentItemCount(start)
                .maxItemCount(end)
                .build();
    }
}
