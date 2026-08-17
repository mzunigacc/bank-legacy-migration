package com.example.banklegacymigration.interest;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class InterestWriter
        implements ItemWriter<InterestAccount> {

    private final JdbcTemplate jdbcTemplate;

    public InterestWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends InterestAccount> accounts) {

        for (InterestAccount account : accounts) {

            jdbcTemplate.update(
                    """
                    INSERT INTO intereses
                    (
                        cuenta_id,
                        nombre,
                        saldo,
                        edad,
                        tipo,
                        interes,
                        saldo_final,
                        anomalia,
                        motivo
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT (cuenta_id) DO NOTHING
                    """,
                    account.getCuentaId(),
                    account.getNombre(),
                    account.getSaldo(),
                    account.getEdad(),
                    account.getTipo(),
                    account.getInteres(),
                    account.getSaldoFinal(),
                    account.isAnomalia(),
                    account.getMotivo()
            );
        }
    }
}