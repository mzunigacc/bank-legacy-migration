package com.example.banklegacymigration.statement;

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
public class StatementJobConfig {

    @Value("${batch.chunk-size}")
    private int chunkSize;

    @Value("${batch.skip-limit}")
    private int skipLimit;

    @Value("${batch.retry-limit}")
    private int retryLimit;

    @Value("${batch.partition.grid-size}")
    private int gridSize;

    @Bean
    public Step statementWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<AnnualStatement> statementItemReader,
            StatementProcessor statementProcessor,
            StatementWriter statementWriter,
            StatementSkipListener statementSkipListener,
            StatementStepExecutionListener statementStepExecutionListener) {

        return new StepBuilder("statementWorkerStep", jobRepository)
                .<AnnualStatement, AnnualStatement>chunk(
                        chunkSize,
                        transactionManager
                )
                .reader(statementItemReader)
                .processor(statementProcessor)
                .writer(statementWriter)

                .faultTolerant()

                .skip(InvalidStatementException.class)
                .skip(FlatFileParseException.class)
                .skipLimit(skipLimit)

                .retry(TransientDataAccessException.class)
                .retryLimit(retryLimit)

                .listener(statementSkipListener)
                .listener(statementStepExecutionListener)

                .build();
    }

    @Bean
    public Step statementPartitionStep(
            JobRepository jobRepository,
            Step statementWorkerStep,
            StatementPartitioner statementPartitioner,
            TaskExecutor batchTaskExecutor) {

        return new StepBuilder("statementPartitionStep", jobRepository)
                .partitioner(
                        "statementWorkerStep",
                        statementPartitioner
                )
                .step(statementWorkerStep)
                .gridSize(gridSize)
                .taskExecutor(batchTaskExecutor)
                .build();
    }

    @Bean
    public Step annualSummaryStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcTemplate jdbcTemplate) {

        return new StepBuilder("annualSummaryStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                    jdbcTemplate.update(
                            """
                            INSERT INTO resumen_anual (
                                cuenta_id,
                                cantidad_movimientos,
                                total_ingresos,
                                total_egresos,
                                saldo_neto,
                                cantidad_anomalias
                            )
                            SELECT
                                cuenta_id,
                                COUNT(*),
                                COALESCE(SUM(
                                    CASE
                                        WHEN movimiento = 'INGRESO'
                                        THEN monto
                                        ELSE 0
                                    END
                                ), 0),
                                COALESCE(SUM(
                                    CASE
                                        WHEN movimiento = 'EGRESO'
                                        THEN ABS(monto)
                                        ELSE 0
                                    END
                                ), 0),
                                COALESCE(SUM(monto), 0),
                                COUNT(*) FILTER (
                                    WHERE anomalia = true
                                )
                            FROM estados_cuenta
                            GROUP BY cuenta_id

                            ON CONFLICT (cuenta_id)
                            DO UPDATE SET
                                cantidad_movimientos = EXCLUDED.cantidad_movimientos,
                                total_ingresos = EXCLUDED.total_ingresos,
                                total_egresos = EXCLUDED.total_egresos,
                                saldo_neto = EXCLUDED.saldo_neto,
                                cantidad_anomalias = EXCLUDED.cantidad_anomalias
                            """
                    );

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job statementJob(
            JobRepository jobRepository,
            Step statementPartitionStep,
            Step annualSummaryStep,
            StatementJobExecutionListener statementJobExecutionListener) {

        return new JobBuilder("statementJob", jobRepository)
                .listener(statementJobExecutionListener)
                .start(statementPartitionStep)
                .next(annualSummaryStep)
                .build();
    }
}