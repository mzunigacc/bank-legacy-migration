package com.example.banklegacymigration.interest;

import java.math.BigDecimal;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class InterestReader {

    @Bean
    @StepScope
    public FlatFileItemReader<InterestAccount> interestItemReader(
            @Value("#{stepExecutionContext['start']}") Integer start,
            @Value("#{stepExecutionContext['end']}") Integer end) {

        return new FlatFileItemReaderBuilder<InterestAccount>()
                .name("interestItemReader")
                .resource(
                        new FileSystemResource(
                                "data/intereses.csv"
                        )
                )
                .linesToSkip(1)
                .delimited()
                .names(
                        "cuenta_id",
                        "nombre",
                        "saldo",
                        "edad",
                        "tipo"
                )
                .fieldSetMapper(fieldSet -> new InterestAccount(
                        fieldSet.readLong("cuenta_id"),
                        fieldSet.readString("nombre"),
                        new BigDecimal(
                                fieldSet.readString("saldo")
                        ),
                        fieldSet.readInt("edad"),
                        fieldSet.readString("tipo")
                ))
                .currentItemCount(start)
                .maxItemCount(end)
                .build();
    }
}