package com.example.banklegacymigration.bff.common.repository;

import com.example.banklegacymigration.bff.common.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Account> findAll() {

        String sql = """
                SELECT cuenta_id, nombre, saldo, edad, tipo, interes, saldo_final
                FROM intereses
                ORDER BY cuenta_id
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new Account(
                                rs.getLong("cuenta_id"),
                                rs.getString("nombre"),
                                rs.getBigDecimal("saldo"),
                                rs.getInt("edad"),
                                rs.getString("tipo"),
                                rs.getBigDecimal("interes"),
                                rs.getBigDecimal("saldo_final")
                        )
        );
    }

    public Optional<Account> findById(Long cuentaId) {

        String sql = """
                SELECT cuenta_id, nombre, saldo, edad, tipo, interes, saldo_final
                FROM intereses
                WHERE cuenta_id = ?
                """;

        List<Account> accounts = jdbcTemplate.query(
                sql,
                (rs, rowNum) ->
                        new Account(
                                rs.getLong("cuenta_id"),
                                rs.getString("nombre"),
                                rs.getBigDecimal("saldo"),
                                rs.getInt("edad"),
                                rs.getString("tipo"),
                                rs.getBigDecimal("interes"),
                                rs.getBigDecimal("saldo_final")
                        ),
                cuentaId
        );

        return accounts.stream().findFirst();
    }

    public int updateBalance(
            Long cuentaId,
            BigDecimal nuevoSaldo) {

        String sql = """
                UPDATE intereses
                SET saldo_final = ?
                WHERE cuenta_id = ?
                """;

        return jdbcTemplate.update(
                sql,
                nuevoSaldo,
                cuentaId
        );
    }

    public void saveWithdrawal(
            Long cuentaId,
            BigDecimal monto) {

        String sql = """
                INSERT INTO retiros_atm (
                    cuenta_id,
                    fecha_hora,
                    monto
                )
                VALUES (?, CURRENT_TIMESTAMP, ?)
                """;

        jdbcTemplate.update(
                sql,
                cuentaId,
                monto
        );
    }
}