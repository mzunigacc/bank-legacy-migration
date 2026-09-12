package com.example.bankcore.dto;

import java.math.BigDecimal;

public class WithdrawalResult {

    private Long cuentaId;
    private BigDecimal monto;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoNuevo;

    public WithdrawalResult(
            Long cuentaId,
            BigDecimal monto,
            BigDecimal saldoAnterior,
            BigDecimal saldoNuevo) {
        this.cuentaId = cuentaId;
        this.monto = monto;
        this.saldoAnterior = saldoAnterior;
        this.saldoNuevo = saldoNuevo;
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