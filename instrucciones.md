# Instrucciones de ejecución y pruebas

## 1. Requisitos

Para ejecutar y validar la solución se requiere:

- Git
- Docker con Docker Compose
- Maven
- `curl`
- Python 3

Python 3 se utiliza únicamente en los comandos de validación para extraer tokens OAuth2 desde respuestas JSON. No es una dependencia de los microservicios.

Validar las herramientas:

```bash
git --version
docker --version
docker compose version
mvn --version
curl --version
python3 --version
```

## 2. Obtener el proyecto

```bash
git clone https://github.com/mzunigacc/bank-legacy-migration.git
cd bank-legacy-migration
```

## 3. Preparar artefactos y construir imágenes

Los tres microservicios de negocio utilizan Dockerfiles multi-stage:

- `account-service`
- `payment-service`
- `customer-service`

Los demás componentes utilizan el JAR generado por Maven. Antes de construir las imágenes, verificar estos artefactos y compilarlos sólo si no existen:

```bash
for module in batch bff-web bff-mobile bff-atm config-server discovery-server authorization-server withdrawal-consumer; do
  if ! ls "$module"/target/*.jar >/dev/null 2>&1; then
    echo "Compilando $module..."
    (cd "$module" && mvn clean package -DskipTests) || exit 1
  else
    echo "OK $module: JAR existente"
  fi
done
```

Construir las imágenes:

```bash
docker compose build
```

Validar Compose:

```bash
docker compose config --quiet && echo "OK compose válido"
```

## 4. Infraestructura y microservicios

Para reducir el consumo de recursos, los componentes se validan progresivamente.

Levantar infraestructura y los tres microservicios de negocio:

```bash
docker compose up -d \
  postgres \
  kafka \
  kafka-init \
  config-server \
  discovery-server \
  authorization-server \
  account-service \
  payment-service \
  customer-service
```

Esperar hasta que los tres microservicios estén saludables:

```bash
for i in {1..40}; do
  HEALTHY=$(docker compose ps \
    account-service \
    payment-service \
    customer-service \
    --format json | grep -c '"Health":"healthy"' || true)

  echo "Intento $i -> microservicios healthy: $HEALTHY/3"

  [ "$HEALTHY" -eq 3 ] && break
  sleep 3
done
```

Comprobar los health checks:

```bash
docker compose exec account-service \
  curl -s http://localhost:8081/actuator/health

docker compose exec payment-service \
  curl -s http://localhost:8082/actuator/health

docker compose exec customer-service \
  curl -s http://localhost:8083/actuator/health
```

La respuesta esperada es:

```text
{"status":"UP"}
```

## 5. OAuth2 y BFF Web

Levantar Web:

```bash
docker compose up -d bff-web
```

Obtener tokens Web y ATM:

```bash
WEB_TOKEN=$(curl -s \
  -u bff-web-client:web-secret \
  -d grant_type=client_credentials \
  -d scope=web \
  http://localhost:9000/oauth2/token \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["access_token"])')

ATM_TOKEN=$(curl -s \
  -u bff-atm-client:atm-secret \
  -d grant_type=client_credentials \
  -d scope=atm \
  http://localhost:9000/oauth2/token \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["access_token"])')

test -n "$WEB_TOKEN" && echo "OK token Web"
test -n "$ATM_TOKEN" && echo "OK token ATM"
```

Esperar hasta que Web responda:

```bash
for i in {1..20}; do
  CODE=$(curl -sk -o /dev/null -w "%{http_code}" \
    https://localhost:8441/api/web/cuentas/101)

  echo "Intento $i -> HTTP $CODE"

  [ "$CODE" != "000" ] && break
  sleep 3
done
```

Validar seguridad por canal:

```bash
echo "Sin token:"
curl -sk -o /dev/null -w "HTTP %{http_code}\n" \
  https://localhost:8441/api/web/cuentas/101

echo "Token ATM en Web:"
curl -sk -o /dev/null -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer $ATM_TOKEN" \
  https://localhost:8441/api/web/cuentas/101

echo "Token Web:"
curl -sk -o /dev/null -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer $WEB_TOKEN" \
  https://localhost:8441/api/web/cuentas/101
```

Resultados esperados:

```text
Sin token:        HTTP 401
Token ATM en Web: HTTP 403
Token Web:        HTTP 200
```

Consultar cuenta y cliente:

```bash
curl -sk \
  -H "Authorization: Bearer $WEB_TOKEN" \
  https://localhost:8441/api/web/cuentas/101

curl -sk \
  -H "Authorization: Bearer $WEB_TOKEN" \
  https://localhost:8441/api/web/clientes/101
```

## 6. BFF Mobile

Detener Web y levantar Mobile:

```bash
docker compose stop bff-web
docker compose up -d bff-mobile
```

Obtener token:

```bash
MOBILE_TOKEN=$(curl -s \
  -u bff-mobile-client:mobile-secret \
  -d grant_type=client_credentials \
  -d scope=mobile \
  http://localhost:9000/oauth2/token \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["access_token"])')
```

Esperar disponibilidad:

```bash
for i in {1..20}; do
  CODE=$(curl -sk -o /dev/null -w "%{http_code}" \
    -H "Authorization: Bearer $MOBILE_TOKEN" \
    https://localhost:8442/api/mobile/cuentas/101)

  echo "Intento $i -> HTTP $CODE"

  [ "$CODE" = "200" ] && break
  sleep 3
done
```

Consultar cuenta:

```bash
curl -sk \
  -H "Authorization: Bearer $MOBILE_TOKEN" \
  https://localhost:8442/api/mobile/cuentas/101
```

La respuesta debe ser HTTP `200` y presentar la cuenta con sus últimos movimientos.

## 7. BFF ATM y Kafka

Detener Mobile y levantar ATM junto al consumidor:

```bash
docker compose stop bff-mobile
docker compose up -d bff-atm withdrawal-consumer
```

Si la variable `ATM_TOKEN` no está disponible en la terminal actual, obtenerla nuevamente:

```bash
ATM_TOKEN=$(curl -s \
  -u bff-atm-client:atm-secret \
  -d grant_type=client_credentials \
  -d scope=atm \
  http://localhost:9000/oauth2/token \
  | python3 -c 'import sys,json; print(json.load(sys.stdin)["access_token"])')
```

Esperar disponibilidad:

```bash
for i in {1..20}; do
  CODE=$(curl -sk -o /dev/null -w "%{http_code}" \
    -H "Authorization: Bearer $ATM_TOKEN" \
    https://localhost:8443/api/atm/cuentas/101/saldo)

  echo "Intento $i -> HTTP $CODE"

  [ "$CODE" = "200" ] && break
  sleep 3
done
```

Consultar saldo:

```bash
curl -sk \
  -H "Authorization: Bearer $ATM_TOKEN" \
  https://localhost:8443/api/atm/cuentas/101/saldo
```

Ejecutar un retiro de prueba:

```bash
curl -ski \
  -X POST \
  -H "Authorization: Bearer $ATM_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"monto":1.00}' \
  https://localhost:8443/api/atm/cuentas/101/retiros
```

La respuesta debe ser HTTP `200`.

Validar el tópico:

```bash
docker compose exec kafka \
  /opt/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:29092 \
  --describe \
  --topic bank.withdrawals
```

`bank.withdrawals` debe presentar tres particiones.

Validar el consumidor:

```bash
docker compose logs --tail=100 withdrawal-consumer
```

Después de que el Consumer Group complete su asignación debe aparecer el evento del retiro, incluyendo la cuenta, monto y saldos anterior/nuevo.

Consultar el Consumer Group:

```bash
docker compose exec kafka \
  /opt/kafka/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:29092 \
  --describe \
  --group withdrawal-audit-group
```

## 8. Resiliencia

Con ATM operativo, comprobar primero la consulta normal:

```bash
curl -sk -o /dev/null -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer $ATM_TOKEN" \
  https://localhost:8443/api/atm/cuentas/101/saldo
```

Resultado esperado:

```text
HTTP 200
```

Detener Account Service:

```bash
docker compose stop account-service
```

Repetir la consulta:

```bash
curl -sk -o /dev/null -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer $ATM_TOKEN" \
  https://localhost:8443/api/atm/cuentas/101/saldo
```

La dependencia no disponible produce una respuesta controlada no exitosa. En la validación final se observó:

```text
HTTP 404
```

Restaurar Account Service:

```bash
docker compose start account-service
```

Esperar recuperación:

```bash
for i in {1..20}; do
  HEALTH=$(docker compose exec -T account-service \
    curl -s http://localhost:8081/actuator/health 2>/dev/null || true)

  echo "Intento $i -> $HEALTH"

  echo "$HEALTH" | grep -q '"status":"UP"' && break
  sleep 3
done
```

Repetir la consulta ATM:

```bash
curl -sk -o /dev/null -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer $ATM_TOKEN" \
  https://localhost:8443/api/atm/cuentas/101/saldo
```

Resultado esperado:

```text
HTTP 200
```

## 9. Escalabilidad horizontal

Para reducir el consumo de memoria durante esta prueba:

```bash
docker compose stop \
  bff-web \
  bff-mobile \
  bff-atm \
  withdrawal-consumer
```

Escalar los tres microservicios:

```bash
docker compose up -d \
  --scale account-service=2 \
  --scale payment-service=2 \
  --scale customer-service=2 \
  account-service \
  payment-service \
  customer-service
```

Esperar las seis instancias:

```bash
for i in {1..40}; do
  HEALTHY=$(docker compose ps \
    account-service \
    payment-service \
    customer-service \
    --format json | grep -c '"Health":"healthy"' || true)

  echo "Intento $i -> réplicas healthy: $HEALTHY/6"

  [ "$HEALTHY" -eq 6 ] && break
  sleep 3
done
```

Comprobar las réplicas:

```bash
docker compose ps \
  account-service \
  payment-service \
  customer-service
```

Deben existir:

```text
Account Service:  2
Payment Service:  2
Customer Service: 2
```

Validar ejecución con usuario no root:

```bash
for service in account-service payment-service customer-service; do
  for container in $(docker compose ps -q "$service"); do
    docker exec "$container" id
  done
done
```

Las imágenes ejecutan los microservicios con el usuario `spring`, no como `root`.

Reducir nuevamente a una réplica:

```bash
docker compose up -d \
  --scale account-service=1 \
  --scale payment-service=1 \
  --scale customer-service=1 \
  account-service \
  payment-service \
  customer-service
```

## 10. Spring Batch

La solución implementa tres jobs sobre los archivos oficiales de `data/semana3/`:

- `transactionJob`: reporte diario de transacciones;
- `interestJob`: cálculo de intereses;
- `statementJob`: generación anual de estados de cuenta.

Los procesos implementan Reader, Processor y Writer, chunks, skip/retry, listeners, particionamiento, ejecución paralela y metadata persistente de Spring Batch.

El directorio `data/` se monta en el contenedor Batch como sólo lectura.

### 10.1 Transaction Job

```bash
docker compose run --rm \
  -e SPRING_BATCH_JOB_ENABLED=true \
  -e SPRING_BATCH_JOB_NAME=transactionJob \
  batch
```

Validar:

```bash
docker compose exec postgres \
  psql -U bankuser -d bank_legacy -c "
    SELECT COUNT(*) AS transacciones
    FROM transacciones;

    SELECT COUNT(DISTINCT fecha) AS dias_procesados
    FROM transacciones;
  "
```

Resultado validado:

```text
transacciones:   392
dias_procesados: 239
```

El procesamiento lee 1000 registros, escribe 392 y omite 608 registros inválidos.

### 10.2 Interest Job

```bash
docker compose run --rm \
  -e SPRING_BATCH_JOB_ENABLED=true \
  -e SPRING_BATCH_JOB_NAME=interestJob \
  batch
```

Validar:

```bash
docker compose exec postgres \
  psql -U bankuser -d bank_legacy -c "
    SELECT COUNT(*) AS intereses
    FROM intereses;

    SELECT COUNT(DISTINCT cuenta_id) AS cuentas_unicas
    FROM intereses;
  "
```

Resultado validado:

```text
intereses:     50
cuentas_unicas: 50
```

El procesamiento lee 1000 registros, escribe 296 y omite 704 registros inválidos.

### 10.3 Statement Job

```bash
docker compose run --rm \
  -e SPRING_BATCH_JOB_ENABLED=true \
  -e SPRING_BATCH_JOB_NAME=statementJob \
  batch
```

Validar:

```bash
docker compose exec postgres \
  psql -U bankuser -d bank_legacy -c "
    SELECT COUNT(*) AS estados_cuenta
    FROM estados_cuenta;

    SELECT COUNT(*) AS resumen_anual
    FROM resumen_anual;
  "
```

Resultado validado:

```text
estados_cuenta: 819
resumen_anual:   20
```

El procesamiento lee 1000 registros, escribe 836 y omite 164 registros inválidos.

### 10.4 Estado de los tres jobs

```bash
docker compose exec postgres \
  psql -U bankuser -d bank_legacy -c "
    SELECT DISTINCT ON (ji.job_name)
      ji.job_name,
      je.status,
      je.exit_code
    FROM batch_job_execution je
    JOIN batch_job_instance ji
      ON ji.job_instance_id = je.job_instance_id
    WHERE ji.job_name IN (
      'transactionJob',
      'interestJob',
      'statementJob'
    )
    ORDER BY ji.job_name, je.job_execution_id DESC;
  "
```

Resultado esperado después de ejecutar los tres:

```text
interestJob     COMPLETED  COMPLETED
statementJob    COMPLETED  COMPLETED
transactionJob  COMPLETED  COMPLETED
```

## 11. Detener o restaurar el entorno

Detener los contenedores conservando los datos:

```bash
docker compose down
```

Eliminar además el volumen PostgreSQL:

```bash
docker compose down -v
```

Al levantar nuevamente PostgreSQL, el snapshot reconstruye el baseline persistente:

```bash
docker compose up -d postgres
```

Baseline validado:

```text
transacciones       392
intereses             50
estados_cuenta       819
resumen_anual         20
retiros_atm           34
payment_operations     0
```

La cuenta 101 queda nuevamente con:

```text
saldo        5000.00
interes       100.00
saldo_final  5100.00
```
