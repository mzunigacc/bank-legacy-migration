package com.example.banklegacymigration.interest;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class InterestProcessor
        implements ItemProcessor<InterestAccount, InterestAccount> {

    private static final BigDecimal TASA_AHORRO =
            new BigDecimal("0.01");

    private static final BigDecimal TASA_PRESTAMO =
            new BigDecimal("0.02");

    @Override
    public InterestAccount process(InterestAccount account) {

        BigDecimal tasa;

        if ("ahorro".equalsIgnoreCase(account.getTipo())) {

            tasa = TASA_AHORRO;

        } else if ("prestamo".equalsIgnoreCase(account.getTipo())) {

            tasa = TASA_PRESTAMO;

        } else {

            account.setAnomalia(true);
            account.setMotivo("Tipo de cuenta no contemplado");
            account.setInteres(BigDecimal.ZERO);
            account.setSaldoFinal(account.getSaldo());

            return account;
        }

        BigDecimal interes =
                account.getSaldo().multiply(tasa);

        BigDecimal saldoFinal =
                account.getSaldo().add(interes);

        account.setInteres(interes);
        account.setSaldoFinal(saldoFinal);

        if (account.getSaldo().compareTo(BigDecimal.ZERO) <= 0) {
            account.setAnomalia(true);
            account.setMotivo("Saldo menor o igual a cero");
        }

        return account;
    }
}