package com.example.banklegacymigration.bff.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "intereses")
public class Account {

    @Id
    @Column(name = "cuenta_id")
    private Long cuentaId;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "edad", nullable = false)
    private Integer edad;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "interes", nullable = false, precision = 15, scale = 2)
    private BigDecimal interes;

    @Column(name = "saldo_final", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoFinal;

    @Column(name = "anomalia", nullable = false)
    private Boolean anomalia;

    @Column(name = "motivo")
    private String motivo;

    protected Account() {
    }

    public Account(
            Long cuentaId,
            String nombre,
            BigDecimal saldo,
            Integer edad,
            String tipo,
            BigDecimal interes,
            BigDecimal saldoFinal,
            Boolean anomalia,
            String motivo) {
        this.cuentaId = cuentaId;
        this.nombre = nombre;
        this.saldo = saldo;
        this.edad = edad;
        this.tipo = tipo;
        this.interes = interes;
        this.saldoFinal = saldoFinal;
        this.anomalia = anomalia;
        this.motivo = motivo;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public Boolean getAnomalia() {
        return anomalia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}