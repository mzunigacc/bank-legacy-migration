package com.example.bffweb.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CoreMovementResponse {

    private Long cuentaId;
    private LocalDate fecha;
    private String transaccion;
    private BigDecimal monto;
    private String descripcion;
    private String movimiento;
    private Boolean anomalia;
    private String motivo;

    public CoreMovementResponse() {
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