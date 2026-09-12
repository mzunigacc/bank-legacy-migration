package com.example.banklegacymigration.interest;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class InterestProcessor
        implements ItemProcessor<InterestAccount, InterestAccount> {

    private static final Logger log =
            LoggerFactory.getLogger(InterestProcessor.class);

    private static final BigDecimal TASA_AHORRO =
            new BigDecimal("0.01");

    private static final BigDecimal TASA_PRESTAMO =
            new BigDecimal("0.02");

    @Override
    public InterestAccount process(InterestAccount account) {

        log.info(
                "Procesando cuenta id={} en hilo={}",
                account.getCuentaId(),
                Thread.currentThread().getName()
        );

        validarCuenta(account);

        if (account.getSaldo().compareTo(BigDecimal.ZERO) <= 0) {
            account.setAnomalia(true);
            account.setMotivo("Saldo menor o igual a cero");
        }

        String tipo = account.getTipo().toLowerCase();

        BigDecimal interes = BigDecimal.ZERO;

        if (tipo.equals("ahorro")) {

            interes = account.getSaldo()
                    .multiply(TASA_AHORRO);

        } else if (tipo.equals("prestamo")) {

            interes = account.getSaldo()
                    .multiply(TASA_PRESTAMO);

        } else if (tipo.equals("hipoteca")) {

            account.setAnomalia(true);
            account.setMotivo("Tipo de cuenta no contemplado");
        }

        account.setInteres(interes);

        account.setSaldoFinal(
                account.getSaldo().add(interes)
        );

        return account;
    }

    private void validarCuenta(InterestAccount account) {

        if (account.getCuentaId() == null
                || account.getCuentaId() <= 0) {

            throw new InvalidInterestAccountException(
                    "ID de cuenta inválido: "
                            + account.getCuentaId()
            );
        }

        if (account.getNombre() == null
                || account.getNombre().isBlank()) {

            throw new InvalidInterestAccountException(
                    "Nombre vacío para cuenta ID: "
                            + account.getCuentaId()
            );
        }

        if (account.getTipo() == null
                || account.getTipo().isBlank()) {

            throw new InvalidInterestAccountException(
                    "Tipo de cuenta vacío para ID: "
                            + account.getCuentaId()
            );
        }

        String tipo = account.getTipo().toLowerCase();

        if (!tipo.equals("ahorro")
                && !tipo.equals("prestamo")
                && !tipo.equals("hipoteca")) {

            throw new InvalidInterestAccountException(
                    "Tipo de cuenta inválido: "
                            + account.getTipo()
                            + " para ID: "
                            + account.getCuentaId()
            );
        }
    }
}