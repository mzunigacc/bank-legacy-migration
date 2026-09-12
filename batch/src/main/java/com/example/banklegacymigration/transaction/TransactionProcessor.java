package com.example.banklegacymigration.transaction;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransactionProcessor
        implements ItemProcessor<Transaction, Transaction> {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionProcessor.class);

    @Override
    public Transaction process(Transaction transaction) {

        log.info(
                "Procesando transacción id={} en hilo={}",
                transaction.getId(),
                Thread.currentThread().getName()
        );

        validarTransaction(transaction);

        if (transaction.getMonto().compareTo(BigDecimal.ZERO) < 0) {

            transaction.setAnomalia(true);
            transaction.setMotivo("Monto negativo");

        } else if (transaction.getMonto().compareTo(BigDecimal.ZERO) == 0) {

            transaction.setAnomalia(true);
            transaction.setMotivo("Monto igual a cero");
        }

        return transaction;
    }

    private void validarTransaction(Transaction transaction) {

        if (transaction.getId() == null || transaction.getId() <= 0) {
            throw new InvalidTransactionException(
                    "ID de transacción inválido: "
                            + transaction.getId()
            );
        }

        if (transaction.getTipo() == null
                || transaction.getTipo().isBlank()) {

            throw new InvalidTransactionException(
                    "Tipo de transacción vacío para ID: "
                            + transaction.getId()
            );
        }

        String tipo = transaction.getTipo().toLowerCase();

        if (!tipo.equals("debito")
                && !tipo.equals("credito")) {

            throw new InvalidTransactionException(
                    "Tipo de transacción no permitido: "
                            + transaction.getTipo()
                            + " para ID: "
                            + transaction.getId()
            );
        }
    }
}