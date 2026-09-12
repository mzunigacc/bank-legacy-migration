package com.example.banklegacymigration.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class StatementSkipListener
        implements SkipListener<AnnualStatement, AnnualStatement> {

    private static final Logger log =
            LoggerFactory.getLogger(StatementSkipListener.class);

    @Override
    public void onSkipInRead(Throwable throwable) {

        log.warn(
                "Registro de estado omitido durante lectura. Motivo: {}",
                throwable.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            AnnualStatement statement,
            Throwable throwable) {

        log.warn(
                "Estado omitido durante procesamiento. cuentaId={}, fecha={}, transaccion={}, motivo={}",
                statement.getCuentaId(),
                statement.getFecha(),
                statement.getTransaccion(),
                throwable.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            AnnualStatement statement,
            Throwable throwable) {

        log.error(
                "Estado omitido durante escritura. cuentaId={}, motivo={}",
                statement.getCuentaId(),
                throwable.getMessage()
        );
    }
}