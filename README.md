# Bank Legacy Migration

Proyecto académico desarrollado para **Desarrollo Backend III (PBY2203)**.

Modernización de procesos bancarios legacy mediante Spring Batch, microservicios independientes, BFF por canal, seguridad OAuth2, resiliencia, mensajería asíncrona y contenedores.

## Arquitectura

    Authorization Server
           OAuth2
             |
       +-----+-----+
       |     |     |
      Web  Mobile  ATM
      BFF   BFF    BFF
       |     |     |
       +-----+-----+
             |
    +--------+--------+
    |        |        |
 Account  Payment  Customer
 Service  Service  Service
    |        |        |
    +--------+--------+
             |
        PostgreSQL

    Payment Service -> Kafka -> withdrawal-consumer

La infraestructura transversal utiliza **Spring Cloud Config**, **Eureka**, **Resilience4j** y **Docker Compose**.

## Componentes

| Componente | Responsabilidad |
|---|---|
| `account-service` | Gestión de cuentas, saldos y movimientos |
| `payment-service` | Retiros, depósitos, transferencias y eventos Kafka |
| `customer-service` | Información y mantenimiento de clientes |
| `bff-web` | Acceso Web a cuentas y clientes |
| `bff-mobile` | Consulta simplificada de cuentas y movimientos |
| `bff-atm` | Consulta de saldo y retiros |
| `authorization-server` | OAuth2 y emisión de JWT |
| `config-server` | Configuración centralizada |
| `discovery-server` | Registro y descubrimiento Eureka |
| `withdrawal-consumer` | Consumo asíncrono de retiros |
| `batch` | Procesamiento de datos financieros legacy |

## Spring Batch

Se migraron tres procesos legacy:

- reporte diario de transacciones;
- cálculo periódico de intereses;
- generación anual de estados de cuenta.

Los jobs utilizan Reader, Processor y Writer, procesamiento por chunks, manejo de excepciones con skip/retry, particionamiento, ejecución paralela, metadata persistente para reinicio e idempotencia mediante UPSERT.

Los datos de entrada se encuentran en `data/semana3/`.

## Seguridad y resiliencia

Los BFF utilizan OAuth2 con **Client Credentials** y scopes independientes:

- Web: `web`
- Mobile: `mobile`
- ATM: `atm`

Los microservicios de negocio validan JWT como Resource Servers.

Las comunicaciones síncronas utilizan Resilience4j con Circuit Breaker y Bulkhead. Retry se reserva para consultas idempotentes y no se aplica automáticamente a operaciones monetarias que modifican estado.

## Kafka

`payment-service` publica eventos asíncronos asociados a operaciones financieras.

- `bank.withdrawals` → `withdrawal-consumer`
- `bank.payments`

`bank.withdrawals` utiliza tres particiones y el grupo `withdrawal-audit-group`.

## Docker

La solución se orquesta mediante `docker-compose.yaml`.

Los tres microservicios de negocio utilizan Dockerfiles multi-stage, ejecución con usuario no root y health checks. Pueden escalar horizontalmente mediante Docker Compose.

## Persistencia

PostgreSQL almacena los datos de negocio y metadata de Spring Batch.

- `database/schema.sql`
- `database/bank_legacy_snapshot.sql`

## Documentación

- `instrucciones.md`: ejecución y pruebas de la solución.
- `despliegue.md`: procedimiento propuesto de despliegue cloud.
- `docs/informe-tecnico.md`: fuente del informe técnico.
- `docs/`: documentación histórica del desarrollo.
- `evidencias_ejecucion/`: evidencias de semanas anteriores.

## Tecnologías

Java 17 · Spring Boot · Spring Batch · Spring Cloud Config · Eureka · Spring Security · Spring Authorization Server · OAuth2/JWT · Resilience4j · Apache Kafka · PostgreSQL · Maven · Docker · Docker Compose

## Repositorio

Código fuente: https://github.com/mzunigacc/bank-legacy-migration
