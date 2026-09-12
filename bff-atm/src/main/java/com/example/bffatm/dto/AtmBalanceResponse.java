package com.example.bffatm.dto;

import java.math.BigDecimal;

public class AtmBalanceResponse {

    private Long cuentaId;
    private BigDecimal saldoDisponible;

    public AtmBalanceResponse(
            Long cuentaId,
            BigDecimal saldoDisponible) {
        this.cuentaId = cuentaId;
        this.saldoDisponible = saldoDisponible;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }
}