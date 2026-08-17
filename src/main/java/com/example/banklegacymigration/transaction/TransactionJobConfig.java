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
import org.springframework.jdbc.core.JdbcTemplate;

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
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job transactionJob(
            JobRepository jobRepository,
            Step transactionStep,
            Step dailySummaryStep) {

        return new JobBuilder("transactionJob", jobRepository)
                .start(transactionStep)
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
                            COUNT(*) FILTER (WHERE anomalia = true)
                        FROM transacciones
                        GROUP BY fecha

                        ON CONFLICT (fecha)
                        DO UPDATE SET
                            cantidad_transacciones = EXCLUDED.cantidad_transacciones,
                            monto_total = EXCLUDED.monto_total,
                            cantidad_anomalias = EXCLUDED.cantidad_anomalias
                        """
                );

                return null;
            }, transactionManager)
            .build();
        }
}