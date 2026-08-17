package com.example.banklegacymigration.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private Long id;
    private LocalDate fecha;
    private BigDecimal monto;
    private String tipo;

    public Transaction() {
    }

    public Transaction(Long id, LocalDate fecha, BigDecimal monto, String tipo) {
        this.id = id;
        this.fecha = fecha;
        this.monto = monto;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", monto=" + monto +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}