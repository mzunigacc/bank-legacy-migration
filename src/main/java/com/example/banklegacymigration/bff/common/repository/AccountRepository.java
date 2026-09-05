package com.example.banklegacymigration.bff.common.repository;

import com.example.banklegacymigration.bff.common.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

        return jdbcTemplate.query(sql, (rs, rowNum) ->
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
}