package com.example.bffatm.dto;

import java.math.BigDecimal;

public class WithdrawalResponse {

    private Long cuentaId;
    private BigDecimal montoRetirado;
    private BigDecimal saldoDisponible;
    private String estado;

    public WithdrawalResponse(
            Long cuentaId,
            BigDecimal montoRetirado,
            BigDecimal saldoDisponible,
            String estado) {
        this.cuentaId = cuentaId;
        this.montoRetirado = montoRetirado;
        this.saldoDisponible = saldoDisponible;
        this.estado = estado;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public BigDecimal getMontoRetirado() {
        return montoRetirado;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public String getEstado() {
        return estado;
    }
}