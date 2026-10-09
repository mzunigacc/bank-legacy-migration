package com.example.banklegacymigration.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransactionProcessor
        implements ItemProcessor<Transaction, Transaction> {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionProcessor.class);

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    @Override
    public Transaction process(Transaction transaction) {

        validarId(transaction);

        transaction.setFecha(parseFecha(
                transaction.getFechaRaw(),
                transaction.getId()
        ));

        transaction.setMonto(parseMonto(
                transaction.getMontoRaw(),
                transaction.getId()
        ));

        transaction.setTipo(normalizarTipo(
                transaction.getTipo(),
                transaction.getId()
        ));

        log.info(
                "Procesada transacción id={} fecha={} monto={} tipo={} hilo={}",
                transaction.getId(),
                transaction.getFecha(),
                transaction.getMonto(),
                transaction.getTipo(),
                Thread.currentThread().getName()
        );

        return transaction;
    }

    private void validarId(Transaction transaction) {

        if (transaction.getId() == null || transaction.getId() <= 0) {
            throw new InvalidTransactionException(
                    "ID de transacción inválido: " + transaction.getId()
            );
        }
    }

    private LocalDate parseFecha(String valor, Long id) {

        if (valor == null || valor.isBlank()) {
            throw new InvalidTransactionException(
                    "Fecha vacía para ID: " + id
            );
        }

        String fecha = valor.trim();

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(fecha, formatter);
            } catch (DateTimeParseException ignored) {
                // Se intenta el siguiente formato permitido.
            }
        }

        throw new InvalidTransactionException(
                "Fecha inválida: " + valor + " para ID: " + id
        );
    }

    private BigDecimal parseMonto(String valor, Long id) {

        if (valor == null || valor.isBlank()) {
            throw new InvalidTransactionException(
                    "Monto vacío para ID: " + id
            );
        }

        try {
            BigDecimal monto = new BigDecimal(valor.trim());

            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTransactionException(
                        "Monto debe ser mayor a cero: "
                                + valor
                                + " para ID: "
                                + id
                );
            }

            return monto;

        } catch (NumberFormatException e) {
            throw new InvalidTransactionException(
                    "Monto inválido: " + valor + " para ID: " + id
            );
        }
    }

    private String normalizarTipo(String valor, Long id) {

        if (valor == null || valor.isBlank()) {
            throw new InvalidTransactionException(
                    "Tipo vacío para ID: " + id
            );
        }

        String tipo = valor
                .trim()
                .toLowerCase()
                .replace("é", "e");

        if (!tipo.equals("credito") && !tipo.equals("debito")) {
            throw new InvalidTransactionException(
                    "Tipo no permitido: " + valor + " para ID: " + id
            );
        }

        return tipo;
    }
}
