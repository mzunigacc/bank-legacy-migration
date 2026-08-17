# Bank Legacy Migration

Proyecto desarrollado con **Java, Spring Boot, Spring Batch y PostgreSQL** para procesar información proveniente de archivos CSV de un sistema bancario legado.

La solución implementa tres procesos Batch independientes:

- Procesamiento de transacciones.
- Cálculo de intereses.
- Generación de estados de cuenta y resúmenes.
- Persistencia de resultados en PostgreSQL.
- Identificación y registro de anomalías durante el procesamiento.

---

## Tecnologías utilizadas

- Java 17
- Spring Boot 3
- Spring Batch
- PostgreSQL
- Maven
- Git / GitHub

---

## Estructura del proyecto

```text
bank-legacy-migration/
├── data/
│   ├── transacciones.csv
│   ├── intereses.csv
│   └── cuentas_anuales.csv
│
├── database/
│   └── schema.sql
│
├── docs/
│   └── evidencias/
│       ├── transaction-job.png
│       ├── interest-job.png
│       └── statement-job.png
│
├── exploration/
│   └── explore_data.py
│
├── src/main/java/com/example/banklegacymigration/
│   ├── transaction/
│   ├── interest/
│   └── statement/
│
├── src/main/resources/
│   └── application.properties
│
├── pom.xml
└── README.md
```

Cada dominio mantiene separados sus componentes principales de Spring Batch:

```text
Reader → Processor → Writer
```

---

## Jobs implementados

### Transaction Job

Procesa el archivo:

```text
data/transacciones.csv
```

El Job realiza:

1. Lectura del archivo CSV.
2. Conversión de cada registro a un objeto `Transaction`.
3. Validación y detección de anomalías.
4. Persistencia en PostgreSQL.
5. Generación de un resumen diario de transacciones.

Las transacciones con montos negativos o iguales a cero se conservan, pero son identificadas mediante los campos:

```text
anomalia
motivo
```

El Job contiene dos Steps:

```text
transactionStep
      ↓
dailySummaryStep
```

El segundo Step genera la tabla:

```text
resumen_transacciones_diarias
```

con cantidad de transacciones, monto total y cantidad de anomalías por fecha.

---

### Interest Job

Procesa el archivo:

```text
data/intereses.csv
```

El Job calcula los intereses correspondientes a cada cuenta.

Para efectos del ejercicio se consideran las siguientes tasas:

- Cuenta de ahorro: 1%.
- Préstamo: 2%.

Además, se calcula el saldo final:

```text
saldo_final = saldo + interes
```

Los saldos menores o iguales a cero y los tipos de cuenta no contemplados son registrados como anomalías.

Los resultados son persistidos en la tabla:

```text
intereses
```

---

### Statement Job

Procesa el archivo:

```text
data/cuentas_anuales.csv
```

Cada movimiento es clasificado según su efecto sobre la cuenta.

Los registros procesados son almacenados en:

```text
estados_cuenta
```

El Job contiene dos Steps:

```text
statementStep
      ↓
annualSummaryStep
```

El segundo Step genera un resumen por cuenta con:

- Cantidad de movimientos.
- Total de ingresos.
- Total de egresos.
- Saldo neto.
- Cantidad de anomalías.

Los resultados se almacenan en:

```text
resumen_anual
```

---

## Manejo de anomalías

El proyecto distingue entre registros procesables y registros que presentan condiciones anómalas.

Cuando un registro puede ser interpretado, se conserva y se marca utilizando:

```text
anomalia = true
motivo = descripción de la anomalía
```

Los Steps utilizan además tolerancia a fallos mediante Spring Batch:

```java
.faultTolerant()
.skip(Exception.class)
.skipLimit(10)
```

Esto permite evitar que un registro con problemas detenga inmediatamente el procesamiento completo del archivo.

---

## Persistencia e idempotencia

Los resultados procesados son almacenados en PostgreSQL.

Para permitir la reejecución de los Jobs sin duplicar registros se utilizan restricciones de clave primaria y operaciones:

```sql
ON CONFLICT ... DO NOTHING
```

Esta estrategia entrega una solución simple de idempotencia utilizando las capacidades de la base de datos relacional, sin incorporar mecanismos adicionales de hashing o deduplicación.

Para las tablas de resumen se utiliza actualización ante conflicto cuando corresponde, permitiendo recalcular los resultados derivados sin generar registros duplicados.

---

## Configuración de PostgreSQL

La conexión se configura en:

```text
src/main/resources/application.properties
```

Ejemplo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bank_legacy
spring.datasource.username=matiaszuniga

spring.batch.jdbc.initialize-schema=always
```

El esquema de las tablas utilizadas por los Jobs se encuentra versionado en:

```text
database/schema.sql
```

---

## Ejecución

### 1. Crear la base de datos

El proyecto utiliza PostgreSQL. Primero se debe crear la base de datos:

```bash
createdb bank_legacy
```

Luego se crean las tablas necesarias utilizando el esquema incluido en el repositorio:

```bash
psql bank_legacy < database/schema.sql
```

Spring Batch crea sus propias tablas de metadatos al iniciar la aplicación mediante:

```properties
spring.batch.jdbc.initialize-schema=always
```

### 2. Seleccionar el Job

Existen tres Jobs independientes.

En:

```text
src/main/resources/application.properties
```

se debe descomentar únicamente el Job que se desea ejecutar:

```properties
# spring.batch.job.name=transactionJob
# spring.batch.job.name=interestJob
# spring.batch.job.name=statementJob
```

Por ejemplo, para ejecutar Transaction:

```properties
spring.batch.job.name=transactionJob
```

### 3. Ejecutar el Job

Desde la raíz del proyecto:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="run.id=10"
```

El parámetro `run.id` permite identificar una nueva instancia de ejecución del Job.

Spring Batch registra la ejecución de cada Job y sus Steps en sus tablas de metadatos.

---

## Tablas generadas

Los procesos almacenan sus resultados en las siguientes tablas:

| Job | Tabla principal | Tabla derivada |
|---|---|---|
| Transaction | `transacciones` | `resumen_transacciones_diarias` |
| Interest | `intereses` | - |
| Statement | `estados_cuenta` | `resumen_anual` |

---

## Evidencias

Las evidencias de ejecución se encuentran en:

```text
docs/evidencias/
```

### Transaction Job

![Transaction Job](docs/evidencias/transaction-job.png)

### Interest Job

![Interest Job](docs/evidencias/interest-job.png)

### Statement Job

![Statement Job](docs/evidencias/statement-job.png)

Cada evidencia muestra la ejecución satisfactoria del Job y los resultados persistidos en PostgreSQL.