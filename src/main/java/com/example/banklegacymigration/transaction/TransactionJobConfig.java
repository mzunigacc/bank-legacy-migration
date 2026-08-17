package com.example.banklegacymigration.transaction;

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
public class TransactionJobConfig {

    @Bean
    public Step transactionStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaction> transactionItemReader,
            TransactionProcessor transactionProcessor,
            TransactionWriter transactionWriter) {

        return new StepBuilder("transactionStep", jobRepository)
                .<Transaction, Transaction>chunk(10, transactionManager)
                .reader(transactionItemReader)
                .processor(transactionProcessor)
                .writer(transactionWriter)
                .build();
    }

    @Bean
    public Job transactionJob(
            JobRepository jobRepository,
            Step transactionStep) {

        return new JobBuilder("transactionJob", jobRepository)
                .start(transactionStep)
                .build();
    }
}