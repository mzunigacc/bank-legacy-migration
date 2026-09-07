# Propuesta Técnica - Semana 4

## Backend for Frontend - Banco XYZ

### 1. Objetivo

Durante la Semana 4 se extiende el proyecto Bank Legacy Migration mediante la implementación del patrón **Backend for Frontend (BFF)**.

El objetivo es proporcionar servicios diferenciados para tres tipos de cliente:

- Web
- Mobile
- ATM

Cada canal presenta necesidades distintas respecto de cantidad de información, operaciones disponibles y seguridad.

---

## 2. Estrategia seleccionada

Se seleccionó una estrategia basada en **endpoints y capas específicas por frontend dentro de una única aplicación Spring Boot**.

La solución mantiene separados controllers, services y DTOs para Web, Mobile y ATM, mientras reutiliza una capa común de modelos, servicios y repositorios.

```text
                    PostgreSQL
                        │
                        ▼
                   BFF Common
                        │
             ┌──────────┼──────────┐
             ▼          ▼          ▼
          Web BFF   Mobile BFF   ATM BFF
```

Esta alternativa permite separar la lógica específica de cada frontend sin duplicar el acceso a datos ni desplegar múltiples aplicaciones para el alcance actual del proyecto.

---

## 3. Organización del código

La implementación se organiza principalmente bajo:

```text
bff/
├── common/
│   ├── model/
│   ├── repository/
│   └── service/
├── web/
│   ├── controller/
│   ├── dto/
│   └── service/
├── mobile/
│   ├── controller/
│   ├── dto/
│   └── service/
├── atm/
│   ├── controller/
│   ├── dto/
│   ├── exception/
│   └── service/
└── security/
```

`common` contiene elementos reutilizados por los distintos canales.

Cada BFF mantiene sus propios DTOs y servicios para adaptar la información al frontend correspondiente.

---

## 4. Personalización por frontend

### Web

El BFF Web entrega una representación completa de una cuenta.

```http
GET /api/web/cuentas/{cuentaId}
```

Incluye datos del titular, tipo de cuenta, saldo, interés generado y movimientos.

### Mobile

El BFF Mobile prioriza una respuesta ligera.

```http
GET /api/mobile/cuentas/{cuentaId}
```

Entrega únicamente identificador, saldo y últimos movimientos con fecha y monto.

La reducción de campos evita enviar al cliente Mobile información que no requiere para su interfaz.

### ATM

El BFF ATM está orientado a operaciones críticas.

```http
GET /api/atm/cuentas/{cuentaId}/saldo
POST /api/atm/cuentas/{cuentaId}/retiros
```

La operación de retiro valida:

1. existencia de la cuenta;
2. monto válido;
3. saldo suficiente.

Si las validaciones se cumplen, actualiza el saldo y registra la operación.

---

## 5. Persistencia e integridad

Los tres BFF utilizan PostgreSQL como fuente común de datos.

La información de cuentas es consultada desde las tablas generadas por los procesos previos del proyecto.

Para los retiros ATM se incorporó:

```text
retiros_atm
```

La actualización del saldo y el registro del retiro se ejecutan mediante `@Transactional`.

De esta forma, si una de las operaciones falla, la transacción completa puede revertirse y se evita persistir solamente una parte de la operación.

El saldo actualizado queda disponible para consultas posteriores desde cualquiera de los tres BFF.

---

## 6. Seguridad

La solución utiliza Spring Security con autenticación HTTP Basic.

Cada canal posee un rol independiente:

```text
ROLE_WEB
ROLE_MOBILE
ROLE_ATM
```

Las rutas se restringen según el canal:

```text
/api/web/**     → ROLE_WEB
/api/mobile/**  → ROLE_MOBILE
/api/atm/**     → ROLE_ATM
```

Esto permite diferenciar entre autenticación y autorización.

Una solicitud sin credenciales válidas genera una respuesta `401 Unauthorized`, mientras que un usuario autenticado que intenta utilizar un BFF correspondiente a otro canal obtiene `403 Forbidden`.

Las credenciales definidas en el proyecto se utilizan exclusivamente para demostración académica.

---

## 7. Manejo de respuestas ATM

Se definieron respuestas HTTP específicas para los principales escenarios de retiro:

| Situación | HTTP |
|---|---:|
| Retiro aprobado | 200 |
| Monto inválido | 400 |
| Cuenta inexistente | 404 |
| Saldo insuficiente | 409 |

Las excepciones de negocio son manejadas mediante un `RestControllerAdvice`.

---

## 8. Validación

La implementación se validó mediante una colección Postman que cubre:

- BFF Web;
- BFF Mobile;
- consulta de saldo ATM;
- retiro aprobado;
- retiro con monto inválido;
- cuenta inexistente;
- saldo insuficiente;
- solicitud sin credenciales;
- intentos de acceso entre roles diferentes.

El resultado final del Collection Runner fue:

```text
20 tests
20 aprobados
0 fallidos
0 errores
```

Las evidencias se encuentran en:

```text
docs/evidencias/
```

---

## 9. Conclusión

La estrategia implementada permite ofrecer contratos distintos para Web, Mobile y ATM manteniendo una fuente de datos compartida.

La separación de controllers, services y DTOs permite adaptar cada respuesta a las necesidades del frontend correspondiente, mientras la capa común evita duplicar lógica de acceso a datos.

La incorporación de Spring Security, manejo específico de errores y transacciones para retiros permite complementar la diferenciación de los BFF con controles de acceso e integridad de datos.