package com.example.bffweb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WebMovementResponse {

    private LocalDate fecha;
    private String tipo;
    private BigDecimal monto;
    private String descripcion;

    public WebMovementResponse(
            LocalDate fecha,
            String tipo,
            BigDecimal monto,
            String descripcion) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public String getDescripcion() {
        return descripcion;
    }
}