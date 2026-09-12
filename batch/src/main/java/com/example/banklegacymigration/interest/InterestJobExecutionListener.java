package com.example.banklegacymigration.interest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class InterestJobExecutionListener
        implements JobExecutionListener {

    private static final Logger log =
            LoggerFactory.getLogger(InterestJobExecutionListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {

        log.info(
                "Inicio Job: {} | parametros={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getJobParameters()
        );
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        log.info(
                "Fin Job: {} | status={} | exitStatus={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                jobExecution.getExitStatus().getExitCode()
        );
    }
}