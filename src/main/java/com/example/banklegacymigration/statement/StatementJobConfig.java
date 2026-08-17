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
import org.springframework.jdbc.core.JdbcTemplate;

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
            Step statementStep,
            Step annualSummaryStep) {

        return new JobBuilder("statementJob", jobRepository)
                .start(statementStep)
                .next(annualSummaryStep)
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
                            COUNT(*) FILTER (WHERE anomalia = true)
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

                return null;
            }, transactionManager)
            .build();
        }
}