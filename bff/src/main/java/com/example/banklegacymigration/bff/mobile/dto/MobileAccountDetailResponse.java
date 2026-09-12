package com.example.banklegacymigration.bff.mobile.dto;

import java.math.BigDecimal;
import java.util.List;

public class MobileAccountDetailResponse {

    private Long cuentaId;
    private BigDecimal saldo;
    private List<MobileMovementResponse> ultimosMovimientos;

    public MobileAccountDetailResponse(
            Long cuentaId,
            BigDecimal saldo,
            List<MobileMovementResponse> ultimosMovimientos) {
        this.cuentaId = cuentaId;
        this.saldo = saldo;
        this.ultimosMovimientos = ultimosMovimientos;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public List<MobileMovementResponse> getUltimosMovimientos() {
        return ultimosMovimientos;
    }
}