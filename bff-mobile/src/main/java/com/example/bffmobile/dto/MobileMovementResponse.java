package com.example.bffmobile.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MobileMovementResponse {

    private LocalDate fecha;
    private BigDecimal monto;

    public MobileMovementResponse(
            LocalDate fecha,
            BigDecimal monto) {
        this.fecha = fecha;
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}