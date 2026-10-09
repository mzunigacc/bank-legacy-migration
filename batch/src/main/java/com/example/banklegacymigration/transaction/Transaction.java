package com.example.banklegacymigration.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private Long id;
    private String fechaRaw;
    private String montoRaw;
    private LocalDate fecha;
    private BigDecimal monto;
    private String tipo;

    public Transaction() {
    }

    public Transaction(Long id, String fechaRaw, String montoRaw, String tipo) {
        this.id = id;
        this.fechaRaw = fechaRaw;
        this.montoRaw = montoRaw;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaRaw() {
        return fechaRaw;
    }

    public void setFechaRaw(String fechaRaw) {
        this.fechaRaw = fechaRaw;
    }

    public String getMontoRaw() {
        return montoRaw;
    }

    public void setMontoRaw(String montoRaw) {
        this.montoRaw = montoRaw;
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
