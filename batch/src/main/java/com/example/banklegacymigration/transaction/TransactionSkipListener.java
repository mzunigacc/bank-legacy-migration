package com.example.banklegacymigration.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionSkipListener
        implements SkipListener<Transaction, Transaction> {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionSkipListener.class);

    @Override
    public void onSkipInRead(Throwable throwable) {

        log.warn(
                "Registro omitido durante lectura. Motivo: {}",
                throwable.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            Transaction transaction,
            Throwable throwable) {

        log.warn(
                "Transacción omitida durante procesamiento. id={}, tipo={}, monto={}, motivo={}",
                transaction.getId(),
                transaction.getTipo(),
                transaction.getMonto(),
                throwable.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            Transaction transaction,
            Throwable throwable) {

        log.error(
                "Transacción omitida durante escritura. id={}, motivo={}",
                transaction.getId(),
                throwable.getMessage()
        );
    }
}