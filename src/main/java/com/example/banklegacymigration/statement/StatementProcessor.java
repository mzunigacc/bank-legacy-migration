package com.example.banklegacymigration.statement;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StatementProcessor
        implements ItemProcessor<AnnualStatement, AnnualStatement> {

    private static final Logger log =
            LoggerFactory.getLogger(StatementProcessor.class);

    @Override
    public AnnualStatement process(AnnualStatement statement) {

        log.info(
                "Procesando estado cuenta={} fecha={} en hilo={}",
                statement.getCuentaId(),
                statement.getFecha(),
                Thread.currentThread().getName()
        );

        validarStatement(statement);

        if (statement.getMonto().compareTo(BigDecimal.ZERO) > 0) {

            statement.setMovimiento("INGRESO");

        } else if (statement.getMonto().compareTo(BigDecimal.ZERO) < 0) {

            statement.setMovimiento("EGRESO");

        } else {

            statement.setMovimiento("SIN_MOVIMIENTO");
            statement.setAnomalia(true);
            statement.setMotivo("Monto igual a cero");
        }

        return statement;
    }

    private void validarStatement(AnnualStatement statement) {

        if (statement.getCuentaId() == null
                || statement.getCuentaId() <= 0) {

            throw new InvalidStatementException(
                    "ID de cuenta inválido: "
                            + statement.getCuentaId()
            );
        }

        if (statement.getTransaccion() == null
                || statement.getTransaccion().isBlank()) {

            throw new InvalidStatementException(
                    "Tipo de transacción vacío para cuenta: "
                            + statement.getCuentaId()
            );
        }

        if (statement.getDescripcion() == null
                || statement.getDescripcion().isBlank()) {

            throw new InvalidStatementException(
                    "Descripción vacía para cuenta: "
                            + statement.getCuentaId()
            );
        }
    }
}