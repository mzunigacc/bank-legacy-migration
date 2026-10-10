package com.example.banklegacymigration.statement;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StatementProcessor
        implements ItemProcessor<AnnualStatement, AnnualStatement> {

    private static final Logger log =
            LoggerFactory.getLogger(StatementProcessor.class);

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    @Override
    public AnnualStatement process(AnnualStatement statement) {

        validarCuenta(statement);

        LocalDate fecha = parseFecha(statement);
        BigDecimal monto = parseMonto(statement);
        String transaccion = normalizarTransaccion(statement);

        String movimiento;

        switch (transaccion) {
            case "deposito" -> {
                movimiento = "INGRESO";
                monto = monto.abs();
            }

            case "retiro", "pago", "compra" -> {
                movimiento = "EGRESO";
                monto = monto.abs().negate();
            }

            default -> throw new InvalidStatementException(
                    "Transacción no procesable: "
                            + statement.getTransaccion()
                            + " para cuenta: "
                            + statement.getCuentaId()
            );
        }

        String descripcion =
                statement.getDescripcion() == null
                        || statement.getDescripcion().isBlank()
                        ? "Sin descripción"
                        : statement.getDescripcion().trim();

        statement.setFecha(fecha);
        statement.setMonto(monto);
        statement.setTransaccion(transaccion);
        statement.setMovimiento(movimiento);
        statement.setDescripcion(descripcion);
        statement.setAnomalia(false);
        statement.setMotivo(null);

        log.info(
                "Procesado estado cuenta={} fecha={} transaccion={} monto={} movimiento={} hilo={}",
                statement.getCuentaId(),
                fecha,
                transaccion,
                monto,
                movimiento,
                Thread.currentThread().getName()
        );

        return statement;
    }

    private void validarCuenta(AnnualStatement statement) {

        if (statement.getCuentaId() == null
                || statement.getCuentaId() <= 0) {

            throw new InvalidStatementException(
                    "ID de cuenta inválido: "
                            + statement.getCuentaId()
            );
        }
    }

    private LocalDate parseFecha(AnnualStatement statement) {

        String raw = statement.getFechaRaw();

        if (raw == null || raw.isBlank()) {
            throw new InvalidStatementException(
                    "Fecha vacía para cuenta: "
                            + statement.getCuentaId()
            );
        }

        String value = raw.trim();

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        throw new InvalidStatementException(
                "Fecha inválida: "
                        + raw
                        + " para cuenta: "
                        + statement.getCuentaId()
        );
    }

    private BigDecimal parseMonto(AnnualStatement statement) {

        String raw = statement.getMontoRaw();

        if (raw == null || raw.isBlank()) {
            throw new InvalidStatementException(
                    "Monto vacío para cuenta: "
                            + statement.getCuentaId()
            );
        }

        try {
            BigDecimal monto = new BigDecimal(raw.trim());

            if (monto.compareTo(BigDecimal.ZERO) == 0) {
                throw new InvalidStatementException(
                        "Monto igual a cero para cuenta: "
                                + statement.getCuentaId()
                );
            }

            return monto;

        } catch (NumberFormatException e) {
            throw new InvalidStatementException(
                    "Monto inválido: "
                            + raw
                            + " para cuenta: "
                            + statement.getCuentaId()
            );
        }
    }

    private String normalizarTransaccion(AnnualStatement statement) {

        if (statement.getTransaccion() == null
                || statement.getTransaccion().isBlank()) {

            throw new InvalidStatementException(
                    "Transacción vacía para cuenta: "
                            + statement.getCuentaId()
            );
        }

        String value = Normalizer.normalize(
                statement.getTransaccion().trim().toLowerCase(),
                Normalizer.Form.NFD
        );

        return value.replaceAll("\\p{M}", "");
    }
}
