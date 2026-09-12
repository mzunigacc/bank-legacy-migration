package com.example.bffatm.client.dto;

import java.math.BigDecimal;

public class CoreAccountResponse {

    private Long cuentaId;
    private String nombre;
    private BigDecimal saldo;
    private Integer edad;
    private String tipo;
    private BigDecimal interes;
    private BigDecimal saldoFinal;
    private Boolean anomalia;
    private String motivo;

    public CoreAccountResponse() {
    }

    public Long getCuentaId() {
        return cuentaId;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public Integer getEdad() {
        return edad;
    }

    public String getTipo() {
        return tipo;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public Boolean getAnomalia() {
        return anomalia;
    }

    public String getMotivo() {
        return motivo;
    }
}