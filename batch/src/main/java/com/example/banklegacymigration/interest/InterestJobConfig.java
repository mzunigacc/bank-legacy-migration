package com.example.banklegacymigration.interest;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class InterestJobConfig {

    @Value("${batch.chunk-size}")
    private int chunkSize;

    @Value("${batch.skip-limit}")
    private int skipLimit;

    @Value("${batch.retry-limit}")
    private int retryLimit;

    @Value("${batch.partition.grid-size}")
    private int gridSize;

    @Bean
    public Step interestWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<InterestAccount> interestItemReader,
            InterestProcessor interestProcessor,
            InterestWriter interestWriter,
            InterestSkipListener interestSkipListener,
            InterestStepExecutionListener interestStepExecutionListener) {

        return new StepBuilder("interestWorkerStep", jobRepository)
                .<InterestAccount, InterestAccount>chunk(
                        chunkSize,
                        transactionManager
                )
                .reader(interestItemReader)
                .processor(interestProcessor)
                .writer(interestWriter)

                .faultTolerant()

                .skip(InvalidInterestAccountException.class)
                .skip(FlatFileParseException.class)
                .skipLimit(skipLimit)

                .retry(TransientDataAccessException.class)
                .retryLimit(retryLimit)

                .listener(interestSkipListener)
                .listener(interestStepExecutionListener)

                .build();
    }

    @Bean
    public Step interestPartitionStep(
            JobRepository jobRepository,
            Step interestWorkerStep,
            InterestPartitioner interestPartitioner,
            TaskExecutor batchTaskExecutor) {

        return new StepBuilder("interestPartitionStep", jobRepository)
                .partitioner(
                        "interestWorkerStep",
                        interestPartitioner
                )
                .step(interestWorkerStep)
                .gridSize(gridSize)
                .taskExecutor(batchTaskExecutor)
                .build();
    }

    @Bean
    public Job interestJob(
            JobRepository jobRepository,
            Step interestPartitionStep,
            InterestJobExecutionListener interestJobExecutionListener) {

        return new JobBuilder("interestJob", jobRepository)
                .listener(interestJobExecutionListener)
                .start(interestPartitionStep)
                .build();
    }
}