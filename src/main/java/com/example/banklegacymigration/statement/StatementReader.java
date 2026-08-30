package com.example.banklegacymigration.statement;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class StatementReader {

    @Bean
    public SynchronizedItemStreamReader<AnnualStatement> statementItemReader() {

        FlatFileItemReader<AnnualStatement> reader =
                new FlatFileItemReaderBuilder<AnnualStatement>()
                        .name("statementItemReader")
                        .resource(
                                new FileSystemResource(
                                        "data/cuentas_anuales.csv"
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
                                LocalDate.parse(
                                        fieldSet.readString("fecha")
                                ),
                                fieldSet.readString("transaccion"),
                                new BigDecimal(
                                        fieldSet.readString("monto")
                                ),
                                fieldSet.readString("descripcion")
                        ))
                        .saveState(false)
                        .build();

        return new SynchronizedItemStreamReaderBuilder<AnnualStatement>()
                .delegate(reader)
                .build();
    }
}