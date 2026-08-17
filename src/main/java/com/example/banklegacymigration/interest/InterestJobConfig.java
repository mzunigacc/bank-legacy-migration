package com.example.banklegacymigration.interest;

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
public class InterestJobConfig {

    @Bean
    public Step interestStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<InterestAccount> interestItemReader,
            InterestProcessor interestProcessor,
            InterestWriter interestWriter) {

        return new StepBuilder("interestStep", jobRepository)
                .<InterestAccount, InterestAccount>chunk(
                        10,
                        transactionManager
                )
                .reader(interestItemReader)
                .processor(interestProcessor)
                .writer(interestWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job interestJob(
            JobRepository jobRepository,
            Step interestStep) {

        return new JobBuilder("interestJob", jobRepository)
                .start(interestStep)
                .build();
    }
}