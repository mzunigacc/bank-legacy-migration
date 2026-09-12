package com.example.banklegacymigration.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class StatementStepExecutionListener
        implements StepExecutionListener {

    private static final Logger log =
            LoggerFactory.getLogger(StatementStepExecutionListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {

        log.info(
                "Inicio Step: {}",
                stepExecution.getStepName()
        );
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        log.info(
                "Fin Step: {} | status={} | read={} | write={} | readSkip={} | processSkip={} | writeSkip={} | commits={} | rollbacks={}",
                stepExecution.getStepName(),
                stepExecution.getStatus(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getReadSkipCount(),
                stepExecution.getProcessSkipCount(),
                stepExecution.getWriteSkipCount(),
                stepExecution.getCommitCount(),
                stepExecution.getRollbackCount()
        );

        return stepExecution.getExitStatus();
    }
}