package com.example.banklegacymigration.statement;

import java.math.BigDecimal;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class StatementProcessor
        implements ItemProcessor<AnnualStatement, AnnualStatement> {

    @Override
    public AnnualStatement process(AnnualStatement statement) {

        int comparacion =
                statement.getMonto().compareTo(BigDecimal.ZERO);

        if (comparacion > 0) {

            statement.setMovimiento("INGRESO");

        } else if (comparacion < 0) {

            statement.setMovimiento("EGRESO");

        } else {

            statement.setMovimiento("SIN_MOVIMIENTO");
            statement.setAnomalia(true);
            statement.setMotivo("Monto igual a cero");
        }

        return statement;
    }
}