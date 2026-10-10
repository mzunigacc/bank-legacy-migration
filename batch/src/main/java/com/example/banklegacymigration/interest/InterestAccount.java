package com.example.banklegacymigration.interest;

import java.math.BigDecimal;

public class InterestAccount {

    private Long cuentaId;
    private String nombre;

    private String saldoRaw;
    private String edadRaw;

    private BigDecimal saldo;
    private Integer edad;
    private String tipo;

    private BigDecimal interes;
    private BigDecimal saldoFinal;

    public InterestAccount() {
    }

    public InterestAccount(
            Long cuentaId,
            String nombre,
            String saldoRaw,
            String edadRaw,
            String tipo) {

        this.cuentaId = cuentaId;
        this.nombre = nombre;
        this.saldoRaw = saldoRaw;
        this.edadRaw = edadRaw;
        this.tipo = tipo;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSaldoRaw() {
        return saldoRaw;
    }

    public void setSaldoRaw(String saldoRaw) {
        this.saldoRaw = saldoRaw;
    }

    public String getEdadRaw() {
        return edadRaw;
    }

    public void setEdadRaw(String edadRaw) {
        this.edadRaw = edadRaw;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
}
