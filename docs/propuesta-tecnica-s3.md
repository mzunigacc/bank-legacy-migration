# Propuesta Técnica - Semana 3

## Bank Legacy Migration

## 1. Objetivo

El proyecto **Bank Legacy Migration** implementa procesos batch para migrar, validar, transformar y persistir información proveniente de archivos CSV de un sistema bancario legacy.

Durante las semanas anteriores se construyeron tres Jobs independientes utilizando Spring Batch:

- `transactionJob`
- `interestJob`
- `statementJob`

La Semana 3 tiene como objetivo evolucionar esta solución mediante técnicas de escalamiento y procesamiento paralelo, manteniendo las políticas de tolerancia a fallos previamente implementadas y evaluando distintas configuraciones para seleccionar una alternativa adecuada al volumen de datos utilizado.

La estrategia seleccionada fue implementar **particionamiento** en los tres Jobs, permitiendo dividir cada archivo de entrada en rangos independientes que pueden ser procesados concurrentemente por distintos workers.

---

## 2. Arquitectura general

Los tres procesos mantienen como unidad fundamental la arquitectura:

```text
Reader → Processor → Writer
```

En Semana 3 esta arquitectura se ejecuta dentro de workers independientes administrados por un Step particionado.

```text
                         Job
                          │
                          ▼
                  Partition Manager
                          │
                    Partitioner
                          │
              ┌───────────┴───────────┐
              ▼                       ▼
         partition0               partition1
     ExecutionContext          ExecutionContext
       start / end              start / end
              │                       │
              └───────────┬───────────┘
                          ▼
                     TaskExecutor
                          │
              ┌───────────┴───────────┐
              ▼                       ▼
           Worker 0                 Worker 1
              │                       │
            Reader                  Reader
              │                       │
          Processor                Processor
              │                       │
            Writer                  Writer
              │                       │
              └───────────┬───────────┘
                          ▼
                     PostgreSQL
```

El `TaskExecutor` se utiliza en el nivel del manager para ejecutar las particiones concurrentemente.

Los workers no poseen un segundo `TaskExecutor`, evitando introducir paralelismo anidado.

---

## 3. Procesos batch

### 3.1 Transaction Job

Fuente:

```text
data/transacciones.csv
```

El proceso valida y transforma transacciones bancarias.

Entre sus reglas se encuentran:

- validación del identificador;
- validación del tipo de transacción;
- detección de montos negativos;
- detección de montos iguales a cero;
- persistencia de anomalías procesables;
- omisión de registros inválidos.

Después del procesamiento particionado se ejecuta:

```text
dailySummaryStep
```

que genera el resumen diario de las transacciones.

Flujo:

```text
transactionPartitionStep
          │
          ▼
transactionWorkerStep
          │
          ▼
  dailySummaryStep
```

---

### 3.2 Interest Job

Fuente:

```text
data/intereses.csv
```

El proceso calcula intereses y saldo final de las cuentas.

Las tasas implementadas son:

```text
ahorro   → 1%
prestamo → 2%
```

Los registros que contienen condiciones conocidas pero no contempladas para cálculo pueden conservarse como anomalías, manteniendo el dato original y registrando el motivo.

Flujo:

```text
interestPartitionStep
        │
        ▼
interestWorkerStep
```

---

### 3.3 Statement Job

Fuente:

```text
data/cuentas_anuales.csv
```

El proceso clasifica cada movimiento como:

```text
INGRESO
EGRESO
SIN_MOVIMIENTO
```

Después del procesamiento particionado se ejecuta:

```text
annualSummaryStep
```

que calcula por cuenta:

- cantidad de movimientos;
- total de ingresos;
- total de egresos;
- saldo neto;
- cantidad de anomalías.

Flujo:

```text
statementPartitionStep
          │
          ▼
 statementWorkerStep
          │
          ▼
  annualSummaryStep
```

---

## 4. Estrategia de particionamiento

Cada dominio posee su propio `Partitioner`.

Su responsabilidad es:

1. contar los registros existentes en el archivo;
2. utilizar el `gridSize` configurado para determinar la cantidad de particiones;
3. calcular el rango correspondiente a cada partición;
4. almacenar los límites en un `ExecutionContext`.

Por ejemplo, para 10 registros y:

```text
gridSize = 2
```

la distribución es:

```text
partition0 → start=0 → end=5
partition1 → start=5 → end=10
```

Cuando la división no es exacta, los registros restantes se distribuyen entre las primeras particiones.

Esta estrategia permite adaptar automáticamente los rangos al tamaño del archivo sin mantener posiciones hardcodeadas.

---

## 5. Reader independiente por partición

Una diferencia importante respecto de la estrategia utilizada anteriormente es que los workers no comparten una única instancia del Reader.

Los `FlatFileItemReader` se encuentran configurados mediante:

```java
@StepScope
```

y reciben desde el `ExecutionContext`:

```java
@Value("#{stepExecutionContext['start']}")
Integer start

@Value("#{stepExecutionContext['end']}")
Integer end
```

Estos valores son utilizados mediante:

```java
.currentItemCount(start)
.maxItemCount(end)
```

Por lo tanto, cada partición posee:

```text
su propio Reader
+
su propio estado
+
su propio rango de registros
```

Esto elimina la necesidad de sincronizar un único Reader compartido entre varios threads.

---

## 6. Procesamiento paralelo

Las particiones son ejecutadas mediante un `ThreadPoolTaskExecutor`.

Los principales parámetros se encuentran externalizados:

```properties
batch.executor.core-pool-size=2
batch.executor.max-pool-size=2
batch.executor.queue-capacity=10

batch.partition.grid-size=2
```

La arquitectura resultante para la configuración predeterminada es:

```text
Partition Manager
       │
       ▼
  TaskExecutor
       │
 ┌─────┴─────┐
 ▼           ▼
thread-1   thread-2
 │           │
 ▼           ▼
partición  partición
```

De esta manera se distribuye el procesamiento sin introducir concurrencia adicional dentro de cada worker.

---

## 7. Procesamiento por chunks

El tamaño del chunk también se encuentra externalizado:

```properties
batch.chunk-size=5
```

Cada worker procesa los registros en grupos y realiza los commits correspondientes de acuerdo con el tamaño configurado y la cantidad de registros asignados a su partición.

Externalizar este parámetro permite evaluar distintas configuraciones sin modificar el código fuente.

---

## 8. Pool de conexiones

La conexión con PostgreSQL utiliza HikariCP.

La configuración utilizada es:

```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=30000
```

El tamaño del pool debe analizarse en conjunto con la cantidad de threads utilizados por el procesamiento batch.

La configuración seleccionada utiliza dos workers concurrentes y mantiene un máximo de cinco conexiones disponibles.

Esto evita configurar un pool innecesariamente pequeño respecto del procesamiento concurrente y deja capacidad para otras operaciones realizadas durante la ejecución del Job.

---

## 9. Tolerancia a fallos

La solución diferencia tres situaciones:

```text
Anomalía procesable
        │
        ▼
Conservar y marcar

Dato inválido
        │
        ▼
       SKIP

Fallo transitorio
        │
        ▼
      RETRY
```

Esta separación evita tratar de la misma forma errores permanentes del dato y problemas potencialmente temporales de infraestructura.

---

## 10. Anomalías procesables

Algunos registros pueden contener valores anómalos sin impedir técnicamente su procesamiento.

En estos casos el registro se conserva y se agregan indicadores como:

```text
anomalia = true
motivo   = descripción
```

Ejemplos observados durante las ejecuciones:

```text
Monto negativo
Monto igual a cero
Saldo menor o igual a cero
Tipo de cuenta no contemplado
```

Esta estrategia permite mantener trazabilidad sobre la información recibida sin eliminar automáticamente datos interpretables.

---

## 11. Política de Skip

Los errores permanentes de datos se controlan mediante políticas específicas de `skip`.

Ejemplo:

```java
.skip(InvalidTransactionException.class)
.skip(FlatFileParseException.class)
.skipLimit(skipLimit)
```

El límite se encuentra externalizado:

```properties
batch.skip-limit=10
```

Se probaron dos escenarios diferentes sobre `transactionJob`.

### 11.1 Error de negocio

Se agregó temporalmente:

```text
11,2024-01-09,500,transferencia
```

El registro podía ser leído correctamente desde el CSV, pero no cumplía las reglas del dominio.

El Processor generó:

```text
InvalidTransactionException
```

y el `SkipListener` registró el motivo.

Las métricas mostraron:

```text
processSkip=1
```

mientras el Job terminó:

```text
COMPLETED
```

### 11.2 Error de lectura

Posteriormente se agregó temporalmente:

```text
12,2024-01-10,NO_ES_NUMERO,debito
```

En este caso el monto no podía convertirse al tipo esperado.

El Reader generó:

```text
FlatFileParseException
```

y las métricas registraron:

```text
readSkip=1
```

La ejecución combinada obtuvo:

```text
partition0
status=COMPLETED
read=6
write=6
readSkip=0
processSkip=0

partition1
status=COMPLETED
read=5
write=4
readSkip=1
processSkip=1
rollbacks=1

Job
status=COMPLETED
```

Por lo tanto, el proceso pudo continuar frente a dos escenarios de fallo diferentes sin comprometer los registros válidos.

Los registros utilizados para estas pruebas fueron temporales y posteriormente se restauró el archivo original.

---

## 12. Política de Retry

Los errores transitorios de acceso a datos se configuran mediante:

```java
.retry(TransientDataAccessException.class)
.retryLimit(retryLimit)
```

con:

```properties
batch.retry-limit=3
```

Esta política está orientada a fallos que potencialmente pueden resolverse al repetir la operación.

La decisión es diferente a la utilizada para un dato inválido:

```text
Dato inválido
→ repetir no solucionará el problema
→ SKIP

Fallo transitorio de acceso a datos
→ una nueva ejecución de la operación puede resultar exitosa
→ RETRY
```

De esta forma se evita realizar reintentos innecesarios sobre errores permanentes de los datos.

---

## 13. Listeners y monitoreo

Los Jobs poseen listeners para registrar su comportamiento.

### JobExecutionListener

Registra:

```text
inicio
parámetros
estado final
exit status
```

### StepExecutionListener

Registra:

```text
read
write
readSkip
processSkip
writeSkip
commits
rollbacks
```

Debido al particionamiento, estas métricas pueden analizarse individualmente para cada worker.

### SkipListener

Registra información sobre el registro omitido y la causa del error.

Los logs permiten así observar tanto el rendimiento como el comportamiento de las políticas de tolerancia a fallos.

---

## 14. Comparación de configuraciones

Para evaluar el efecto de los parámetros de ejecución se utilizaron cuatro configuraciones sobre `transactionJob`.

Cada configuración fue ejecutada tres veces.

| Configuración | Grid | Threads | Chunk | Ejecución 1 | Ejecución 2 | Ejecución 3 | Mediana |
|---|---:|---:|---:|---:|---:|---:|---:|
| A | 1 | 1 | 5 | 1040 ms | 84 ms | 1069 ms | 1040 ms |
| B | 2 | 2 | 5 | 78 ms | 86 ms | 133 ms | **86 ms** |
| C | 3 | 3 | 5 | 97 ms | 117 ms | 97 ms | 97 ms |
| D | 3 | 3 | 10 | 125 ms | 101 ms | 128 ms | 125 ms |

Para comparar las ejecuciones se utilizó el tiempo reportado por Spring Batch para el Job.

No se utilizó el tiempo total reportado por Maven, ya que este incorpora tareas adicionales como inicialización de la aplicación y ejecución del plugin.

---

## 15. Análisis de resultados

### Configuración A

```text
grid=1
threads=1
chunk=5
mediana=1040 ms
```

La configuración sin procesamiento paralelo presentó la mayor variabilidad de las pruebas realizadas.

Dos de sus tres ejecuciones superaron un segundo, mientras otra ejecución alcanzó 84 ms.

Debido a esta variabilidad no se atribuye el resultado a una causa técnica específica sin mediciones adicionales.

### Configuración B

```text
grid=2
threads=2
chunk=5
mediana=86 ms
```

Presentó la menor mediana y tiempos relativamente estables.

### Configuración C

```text
grid=3
threads=3
chunk=5
mediana=97 ms
```

Presentó un rendimiento cercano a B, pero no produjo una mejora para el volumen actual.

Con solamente 10 registros, una tercera partición deja aproximadamente 3 o 4 elementos por worker.

### Configuración D

```text
grid=3
threads=3
chunk=10
mediana=125 ms
```

Aumentar el tamaño del chunk a 10 tampoco produjo una mejora para este conjunto de datos.

Debido a que cada partición contiene solamente 3 o 4 registros, el tamaño máximo del chunk no llega a aprovecharse completamente.

Esto no permite concluir que un chunk de tamaño 10 sea inherentemente menos eficiente; solamente indica que no aportó beneficios en esta prueba.

---

## 16. Configuración seleccionada

A partir de los resultados se seleccionó:

```properties
batch.chunk-size=5

batch.executor.core-pool-size=2
batch.executor.max-pool-size=2

batch.partition.grid-size=2
```

La configuración B obtuvo la menor mediana:

```text
86 ms
```

y presentó un comportamiento adecuado para el volumen actual.

La selección no supone que dos threads sean siempre superiores a tres.

La configuración óptima depende de factores como:

- volumen de registros;
- costo del Processor;
- costo de escritura;
- capacidad del pool de conexiones;
- recursos de hardware;
- overhead asociado al particionamiento.

Por esta razón los parámetros permanecen externalizados.

---

## 17. Persistencia e idempotencia

Las tablas de detalle utilizan restricciones relacionales junto con:

```sql
ON CONFLICT (...) DO NOTHING
```

para evitar duplicados.

Las tablas derivadas utilizan:

```sql
ON CONFLICT (...) DO UPDATE
```

permitiendo actualizar los resúmenes correspondientes.

Esta estrategia permite reejecutar los archivos sin insertar nuevamente registros que ya se encuentran persistidos.

Durante la validación final, `transactionJob` fue ejecutado nuevamente sobre los datos originales ya existentes.

La tabla final mantuvo:

```text
10 registros
```

correspondientes a los 10 registros del archivo fuente, sin duplicaciones.

---

## 18. Validación de resultados

Las tablas finales fueron consultadas directamente en PostgreSQL.

### Transacciones

```text
10 registros persistidos
2 anomalías identificadas
```

Las anomalías corresponden a:

```text
Monto negativo
Monto igual a cero
```

### Intereses

```text
8 cuentas procesadas
```

Se conservaron además anomalías como:

```text
Saldo menor o igual a cero
Tipo de cuenta no contemplado
```

### Estados de cuenta

```text
9 movimientos procesados
```

Los movimientos fueron clasificados como:

```text
INGRESO
EGRESO
SIN_MOVIMIENTO
```

### Resumen anual

```text
8 cuentas resumidas
```

El resumen contiene cantidad de movimientos, ingresos, egresos, saldo neto y anomalías.

---

## 19. Configuración final

Los principales parámetros utilizados son:

```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=30000

batch.chunk-size=5
batch.skip-limit=10
batch.retry-limit=3

batch.executor.core-pool-size=2
batch.executor.max-pool-size=2
batch.executor.queue-capacity=10

batch.partition.grid-size=2
```

Esta configuración centraliza los parámetros que pueden requerir ajustes según el entorno o el volumen de procesamiento.

---

## 20. Evidencias

Las evidencias utilizadas son:

```text
s3-01-transaction-particionado.png
s3-02-fault-tolerance.png
s3-03-persistencia-anomalias.png
s3-04-resumenes.png
s3-05-configuracion-versionamiento.png
s3-06-comparacion-rendimiento.png
```

Cada captura fue seleccionada para respaldar varios aspectos de la implementación de manera simultánea y reducir evidencia redundante.

Las evidencias de ejecución se almacenan en:

```text
docs/evidencias/
```

Las evidencias de Semana 3 buscan demostrar de forma conjunta:

- ejecución correcta de los Jobs;
- procesamiento paralelo;
- distribución entre particiones;
- uso de múltiples threads;
- métricas de lectura y escritura;
- continuidad frente a registros inválidos;
- `readSkip` y `processSkip`;
- estado `COMPLETED`;
- persistencia de resultados;
- identificación de anomalías;
- comparación de configuraciones.

Se priorizan capturas que permitan observar varios de estos elementos simultáneamente para evitar evidencia redundante.

---

## 21. Conclusión

La evolución realizada en Semana 3 reemplaza el modelo de lectura concurrente compartida utilizado anteriormente por una arquitectura particionada.

Cada worker posee ahora un Reader independiente y procesa exclusivamente el rango asignado mediante su `ExecutionContext`.

La solución combina:

```text
particionamiento
+
procesamiento por chunks
+
ejecución paralela
+
tolerancia a fallos
+
monitoreo
+
persistencia idempotente
```

Las pruebas demostraron además que aumentar el número de threads no implica automáticamente reducir el tiempo de ejecución.

Para el volumen actual, la configuración:

```text
grid-size = 2
threads   = 2
chunk     = 5
```

presentó el mejor resultado mediano entre las configuraciones evaluadas.

Al mantener estos parámetros externalizados, la solución puede ser ajustada posteriormente para cargas mayores sin modificar la arquitectura ni recompilar el código.