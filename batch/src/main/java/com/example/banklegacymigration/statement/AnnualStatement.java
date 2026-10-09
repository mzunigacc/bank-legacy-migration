package com.example.banklegacymigration.statement;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AnnualStatement {

    private Long cuentaId;

    private String fechaRaw;
    private String montoRaw;

    private LocalDate fecha;
    private String transaccion;
    private BigDecimal monto;
    private String descripcion;

    private String movimiento;
    private boolean anomalia;
    private String motivo;

    public AnnualStatement() {
    }

    public AnnualStatement(
            Long cuentaId,
            String fechaRaw,
            String transaccion,
            String montoRaw,
            String descripcion) {

        this.cuentaId = cuentaId;
        this.fechaRaw = fechaRaw;
        this.transaccion = transaccion;
        this.montoRaw = montoRaw;
        this.descripcion = descripcion;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(Long cuentaId) {
        this.cuentaId = cuentaId;
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

    public String getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(String transaccion) {
        this.transaccion = transaccion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public boolean isAnomalia() {
        return anomalia;
    }

    public void setAnomalia(boolean anomalia) {
        this.anomalia = anomalia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
