package com.example.bffatm.client.dto;

import java.math.BigDecimal;

public class CoreWithdrawalRequest {

    private BigDecimal monto;

    public CoreWithdrawalRequest(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getMonto() {
        return monto;
    }
}