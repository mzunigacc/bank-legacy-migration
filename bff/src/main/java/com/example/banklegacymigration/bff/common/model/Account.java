package com.example.banklegacymigration.bff.common.model;

import java.math.BigDecimal;

public class Account {

    private Long cuentaId;
    private String nombre;
    private BigDecimal saldo;
    private Integer edad;
    private String tipo;
    private BigDecimal interes;
    private BigDecimal saldoFinal;

    public Account(
            Long cuentaId,
            String nombre,
            BigDecimal saldo,
            Integer edad,
            String tipo,
            BigDecimal interes,
            BigDecimal saldoFinal) {
        this.cuentaId = cuentaId;
        this.nombre = nombre;
        this.saldo = saldo;
        this.edad = edad;
        this.tipo = tipo;
        this.interes = interes;
        this.saldoFinal = saldoFinal;
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
}