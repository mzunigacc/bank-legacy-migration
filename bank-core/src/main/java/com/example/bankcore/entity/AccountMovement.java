package com.example.bankcore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "estados_cuenta")
@IdClass(AccountMovementId.class)
public class AccountMovement {

    @Id
    @Column(name = "cuenta_id")
    private Long cuentaId;

    @Id
    @Column(name = "fecha")
    private LocalDate fecha;

    @Id
    @Column(name = "transaccion")
    private String transaccion;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "movimiento", nullable = false)
    private String movimiento;

    @Column(name = "anomalia", nullable = false)
    private Boolean anomalia;

    @Column(name = "motivo")
    private String motivo;

    protected AccountMovement() {
    }

    public AccountMovement(
            Long cuentaId,
            LocalDate fecha,
            String transaccion,
            BigDecimal monto,
            String descripcion,
            String movimiento,
            Boolean anomalia,
            String motivo) {
        this.cuentaId = cuentaId;
        this.fecha = fecha;
        this.transaccion = transaccion;
        this.monto = monto;
        this.descripcion = descripcion;
        this.movimiento = movimiento;
        this.anomalia = anomalia;
        this.motivo = motivo;
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

    public Boolean getAnomalia() {
        return anomalia;
    }

    public String getMotivo() {
        return motivo;
    }
}