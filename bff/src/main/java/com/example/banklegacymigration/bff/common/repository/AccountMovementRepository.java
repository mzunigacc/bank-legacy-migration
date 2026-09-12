package com.example.banklegacymigration.bff.common.repository;

import com.example.banklegacymigration.bff.common.model.AccountMovement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountMovementRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountMovementRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AccountMovement> findByAccountId(Long cuentaId) {

        String sql = """
                SELECT cuenta_id, fecha, transaccion, monto, descripcion, movimiento
                FROM estados_cuenta
                WHERE cuenta_id = ?
                ORDER BY fecha DESC, transaccion DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new AccountMovement(
                                rs.getLong("cuenta_id"),
                                rs.getDate("fecha").toLocalDate(),
                                rs.getString("transaccion"),
                                rs.getBigDecimal("monto"),
                                rs.getString("descripcion"),
                                rs.getString("movimiento")
                        ),
                cuentaId
        );
    }
}