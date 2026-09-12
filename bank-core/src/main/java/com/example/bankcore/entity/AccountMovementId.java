package com.example.bankcore.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class AccountMovementId implements Serializable {

    private Long cuentaId;
    private LocalDate fecha;
    private String transaccion;

    public AccountMovementId() {
    }

    public AccountMovementId(
            Long cuentaId,
            LocalDate fecha,
            String transaccion) {
        this.cuentaId = cuentaId;
        this.fecha = fecha;
        this.transaccion = transaccion;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof AccountMovementId that)) {
            return false;
        }

        return Objects.equals(cuentaId, that.cuentaId)
                && Objects.equals(fecha, that.fecha)
                && Objects.equals(transaccion, that.transaccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                cuentaId,
                fecha,
                transaccion
        );
    }
}