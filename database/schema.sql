-- =========================================================
-- Bank Legacy Migration
-- Esquema de tablas de negocio
-- PostgreSQL
-- =========================================================


-- =========================================================
-- 1. TRANSACCIONES
-- =========================================================

CREATE TABLE IF NOT EXISTS transacciones (
    id BIGINT PRIMARY KEY,
    fecha DATE NOT NULL,
    monto NUMERIC(15,2) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    anomalia BOOLEAN NOT NULL,
    motivo VARCHAR(255)
);


-- =========================================================
-- 2. RESUMEN DE TRANSACCIONES DIARIAS
-- =========================================================

CREATE TABLE IF NOT EXISTS resumen_transacciones_diarias (
    fecha DATE PRIMARY KEY,
    cantidad_transacciones INTEGER NOT NULL,
    monto_total NUMERIC(15,2) NOT NULL,
    cantidad_anomalias INTEGER NOT NULL
);


-- =========================================================
-- 3. INTERESES
-- =========================================================

CREATE TABLE IF NOT EXISTS intereses (
    cuenta_id BIGINT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    saldo NUMERIC(15,2) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    interes NUMERIC(15,2) NOT NULL,
    saldo_final NUMERIC(15,2) NOT NULL,
    anomalia BOOLEAN NOT NULL,
    motivo VARCHAR(255)
);


-- =========================================================
-- 4. ESTADOS DE CUENTA
-- =========================================================

CREATE TABLE IF NOT EXISTS estados_cuenta (
    cuenta_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    transaccion VARCHAR(50) NOT NULL,
    monto NUMERIC(15,2) NOT NULL,
    movimiento VARCHAR(50) NOT NULL,
    anomalia BOOLEAN NOT NULL,
    motivo VARCHAR(255),

    PRIMARY KEY (cuenta_id, fecha, transaccion, monto)
);


-- =========================================================
-- 5. RESUMEN ANUAL
-- =========================================================

CREATE TABLE IF NOT EXISTS resumen_anual (
    cuenta_id BIGINT PRIMARY KEY,
    cantidad_movimientos INTEGER NOT NULL,
    total_ingresos NUMERIC(15,2) NOT NULL,
    total_egresos NUMERIC(15,2) NOT NULL,
    saldo_neto NUMERIC(15,2) NOT NULL,
    cantidad_anomalias INTEGER NOT NULL
);