package com.example.banklegacymigration.bff.atm.dto;

import java.math.BigDecimal;

public class WithdrawalRequest {

    private BigDecimal monto;

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
}