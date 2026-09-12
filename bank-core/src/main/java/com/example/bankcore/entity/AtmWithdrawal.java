package com.example.bankcore.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "retiros_atm")
public class AtmWithdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_id", nullable = false)
    private Long cuentaId;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    protected AtmWithdrawal() {
    }

    public AtmWithdrawal(
            Long cuentaId,
            LocalDateTime fechaHora,
            BigDecimal monto) {
        this.cuentaId = cuentaId;
        this.fechaHora = fechaHora;
        this.monto = monto;
    }

    public Long getId() {
        return id;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}