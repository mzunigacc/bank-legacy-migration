package com.example.banklegacymigration.statement;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatementWriter
        implements ItemWriter<AnnualStatement> {

    private final JdbcTemplate jdbcTemplate;

    public StatementWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends AnnualStatement> statements) {

        for (AnnualStatement statement : statements) {

            jdbcTemplate.update(
                    """
                    INSERT INTO estados_cuenta
                    (
                        cuenta_id,
                        fecha,
                        transaccion,
                        monto,
                        descripcion,
                        movimiento,
                        anomalia,
                        motivo
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT (cuenta_id, fecha, transaccion)
                    DO NOTHING
                    """,
                    statement.getCuentaId(),
                    statement.getFecha(),
                    statement.getTransaccion(),
                    statement.getMonto(),
                    statement.getDescripcion(),
                    statement.getMovimiento(),
                    statement.isAnomalia(),
                    statement.getMotivo()
            );
        }
    }
}