package com.example.banklegacymigration.interest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class InterestSkipListener
        implements SkipListener<InterestAccount, InterestAccount> {

    private static final Logger log =
            LoggerFactory.getLogger(InterestSkipListener.class);

    @Override
    public void onSkipInRead(Throwable throwable) {

        log.warn(
                "Registro de interés omitido durante lectura. Motivo: {}",
                throwable.getMessage()
        );
    }

    @Override
    public void onSkipInProcess(
            InterestAccount account,
            Throwable throwable) {

        log.warn(
                "Cuenta omitida durante procesamiento. cuentaId={}, tipo={}, saldo={}, motivo={}",
                account.getCuentaId(),
                account.getTipo(),
                account.getSaldo(),
                throwable.getMessage()
        );
    }

    @Override
    public void onSkipInWrite(
            InterestAccount account,
            Throwable throwable) {

        log.error(
                "Cuenta omitida durante escritura. cuentaId={}, motivo={}",
                account.getCuentaId(),
                throwable.getMessage()
        );
    }
}