package com.example.banklegacymigration.transaction;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionJobConfig {

    @Value("${batch.chunk-size}")
    private int chunkSize;

    @Value("${batch.skip-limit}")
    private int skipLimit;

    @Value("${batch.retry-limit}")
    private int retryLimit;

    @Value("${batch.partition.grid-size}")
    private int gridSize;

    @Bean
    public Step transactionWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaction> transactionItemReader,
            TransactionProcessor transactionProcessor,
            TransactionWriter transactionWriter,
            TransactionSkipListener transactionSkipListener,
            TransactionStepExecutionListener transactionStepExecutionListener) {

        return new StepBuilder("transactionWorkerStep", jobRepository)
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)

                .reader(transactionItemReader)
                .processor(transactionProcessor)
                .writer(transactionWriter)

                .faultTolerant()

                .skip(InvalidTransactionException.class)
                .skip(FlatFileParseException.class)
                .skipLimit(skipLimit)

                .retry(TransientDataAccessException.class)
                .retryLimit(retryLimit)

                .listener(transactionSkipListener)
                .listener(transactionStepExecutionListener)

                .build();
    }

    @Bean
    public Step transactionPartitionStep(
            JobRepository jobRepository,
            Step transactionWorkerStep,
            TransactionPartitioner transactionPartitioner,
            TaskExecutor batchTaskExecutor) {

        return new StepBuilder("transactionPartitionStep", jobRepository)
                .partitioner(
                        "transactionWorkerStep",
                        transactionPartitioner
                )
                .step(transactionWorkerStep)
                .gridSize(gridSize)
                .taskExecutor(batchTaskExecutor)
                .build();
    }

    @Bean
    public Job transactionJob(
            JobRepository jobRepository,
            Step transactionPartitionStep,
            Step dailySummaryStep,
            TransactionJobExecutionListener transactionJobExecutionListener) {

        return new JobBuilder("transactionJob", jobRepository)
                .listener(transactionJobExecutionListener)
                .start(transactionPartitionStep)
                .next(dailySummaryStep)
                .build();
    }

    @Bean
    public Step dailySummaryStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcTemplate jdbcTemplate) {

        return new StepBuilder("dailySummaryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    jdbcTemplate.update(
                            """
                            INSERT INTO resumen_transacciones_diarias (
                                fecha,
                                cantidad_transacciones,
                                monto_total,
                                cantidad_anomalias
                            )
                            SELECT
                                fecha,
                                COUNT(*),
                                COALESCE(SUM(monto), 0),
                                COUNT(*) FILTER (
                                    WHERE anomalia = true
                                )
                            FROM transacciones
                            GROUP BY fecha

                            ON CONFLICT (fecha)
                            DO UPDATE SET
                                cantidad_transacciones =
                                    EXCLUDED.cantidad_transacciones,
                                monto_total =
                                    EXCLUDED.monto_total,
                                cantidad_anomalias =
                                    EXCLUDED.cantidad_anomalias
                            """
                    );

                    return RepeatStatus.FINISHED;

                }, transactionManager)
                .build();
    }
}