package com.example.banklegacymigration.interest;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        validarIdentidad(account);

        BigDecimal saldo = parseSaldo(account);
        Integer edad = parseEdad(account);
        String tipo = normalizarTipo(account);

        BigDecimal tasa;

        if ("ahorro".equals(tipo)) {
            tasa = TASA_AHORRO;
        } else if ("prestamo".equals(tipo)) {
            tasa = TASA_PRESTAMO;
        } else {
            throw new InvalidInterestAccountException(
                    "Tipo de cuenta no procesable: "
                            + account.getTipo()
                            + " para ID: "
                            + account.getCuentaId()
            );
        }

        BigDecimal interes = saldo
                .multiply(tasa)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoFinal = saldo
                .add(interes)
                .setScale(2, RoundingMode.HALF_UP);

        account.setSaldo(saldo);
        account.setEdad(edad);
        account.setTipo(tipo);
        account.setInteres(interes);
        account.setSaldoFinal(saldoFinal);

        log.info(
                "Procesada cuenta id={} saldo={} edad={} tipo={} interes={} hilo={}",
                account.getCuentaId(),
                saldo,
                edad,
                tipo,
                interes,
                Thread.currentThread().getName()
        );

        return account;
    }

    private void validarIdentidad(InterestAccount account) {

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

        account.setNombre(account.getNombre().trim());
    }

    private BigDecimal parseSaldo(InterestAccount account) {

        String raw = account.getSaldoRaw();

        if (raw == null || raw.isBlank()) {
            throw new InvalidInterestAccountException(
                    "Saldo vacío para ID: "
                            + account.getCuentaId()
            );
        }

        try {
            BigDecimal saldo = new BigDecimal(raw.trim());

            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new InvalidInterestAccountException(
                        "Saldo negativo: "
                                + raw
                                + " para ID: "
                                + account.getCuentaId()
                );
            }

            return saldo;

        } catch (NumberFormatException e) {
            throw new InvalidInterestAccountException(
                    "Saldo inválido: "
                            + raw
                            + " para ID: "
                            + account.getCuentaId()
            );
        }
    }

    private Integer parseEdad(InterestAccount account) {

        String raw = account.getEdadRaw();

        if (raw == null || raw.isBlank()) {
            throw new InvalidInterestAccountException(
                    "Edad vacía para ID: "
                            + account.getCuentaId()
            );
        }

        try {
            int edad = Integer.parseInt(raw.trim());

            if (edad < 18 || edad > 100) {
                throw new InvalidInterestAccountException(
                        "Edad fuera de rango: "
                                + edad
                                + " para ID: "
                                + account.getCuentaId()
                );
            }

            return edad;

        } catch (NumberFormatException e) {
            throw new InvalidInterestAccountException(
                    "Edad inválida: "
                            + raw
                            + " para ID: "
                            + account.getCuentaId()
            );
        }
    }

    private String normalizarTipo(InterestAccount account) {

        if (account.getTipo() == null
                || account.getTipo().isBlank()) {

            throw new InvalidInterestAccountException(
                    "Tipo vacío para ID: "
                            + account.getCuentaId()
            );
        }

        return account.getTipo()
                .trim()
                .toLowerCase();
    }
}
