package com.example.accountservice.entity;

import jakarta.persistence.*;

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
