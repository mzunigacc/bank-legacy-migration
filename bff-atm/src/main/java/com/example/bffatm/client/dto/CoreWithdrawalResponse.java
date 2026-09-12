package com.example.bffatm.client.dto;

import java.math.BigDecimal;

public class CoreWithdrawalResponse {

    private Long cuentaId;
    private BigDecimal monto;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;

    public CoreWithdrawalResponse() {
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public BigDecimal getSaldoNuevo() {
        return saldoNuevo;
    }
}