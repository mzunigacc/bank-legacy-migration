package com.example.banklegacymigration.transaction;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionWriter implements ItemWriter<Transaction> {

    private final JdbcTemplate jdbcTemplate;

    public TransactionWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends Transaction> transactions) {

        for (Transaction transaction : transactions) {

            jdbcTemplate.update(
                    """
                    INSERT INTO transacciones
                    (id, fecha, monto, tipo, anomalia, motivo)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    transaction.getId(),
                    transaction.getFecha(),
                    transaction.getMonto(),
                    transaction.getTipo(),
                    transaction.isAnomalia(),
                    transaction.getMotivo()
            );
        }
    }
}