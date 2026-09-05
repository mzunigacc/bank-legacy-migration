package com.example.banklegacymigration.bff.common.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountMovement {

    private Long id;
    private Long cuentaId;
    private LocalDate fecha;
    private String transaccion;
    private BigDecimal monto;
    private String descripcion;
    private String movimiento;

    public AccountMovement(
            Long id,
            Long cuentaId,
            LocalDate fecha,
            String transaccion,
            BigDecimal monto,
            String descripcion,
            String movimiento) {
        this.id = id;
        this.cuentaId = cuentaId;
        this.fecha = fecha;
        this.transaccion = transaccion;
        this.monto = monto;
        this.descripcion = descripcion;
        this.movimiento = movimiento;
    }

    public Long getId() {
        return id;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTransaccion() {
        return transaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getMovimiento() {
        return movimiento;
    }
}