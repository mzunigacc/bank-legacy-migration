package com.example.banklegacymigration.statement;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class StatementJobConfig {

    @Bean
    public Step statementStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<AnnualStatement> statementItemReader,
            StatementProcessor statementProcessor,
            StatementWriter statementWriter) {

        return new StepBuilder("statementStep", jobRepository)
                .<AnnualStatement, AnnualStatement>chunk(
                        10,
                        transactionManager
                )
                .reader(statementItemReader)
                .processor(statementProcessor)
                .writer(statementWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job statementJob(
            JobRepository jobRepository,
            Step statementStep) {

        return new JobBuilder("statementJob", jobRepository)
                .start(statementStep)
                .build();
    }
}