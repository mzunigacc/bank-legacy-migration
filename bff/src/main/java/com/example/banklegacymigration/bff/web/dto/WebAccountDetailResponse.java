package com.example.banklegacymigration.bff.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class WebAccountDetailResponse {

    private Long cuentaId;
    private String titular;
    private Integer edad;
    private String tipo;
    private BigDecimal saldo;
    private BigDecimal interesGenerado;
    private List<WebMovementResponse> movimientos;

    public WebAccountDetailResponse(
            Long cuentaId,
            String titular,
            Integer edad,
            String tipo,
            BigDecimal saldo,
            BigDecimal interesGenerado,
            List<WebMovementResponse> movimientos) {
        this.cuentaId = cuentaId;
        this.titular = titular;
        this.edad = edad;
        this.tipo = tipo;
        this.saldo = saldo;
        this.interesGenerado = interesGenerado;
        this.movimientos = movimientos;
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public String getTitular() {
        return titular;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public BigDecimal getInteresGenerado() {
        return interesGenerado;
    }

    public List<WebMovementResponse> getMovimientos() {
        return movimientos;
    }
}