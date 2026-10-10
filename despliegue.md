# Despliegue cloud

## 1. Objetivo

La solución se encuentra containerizada y orquestada mediante Docker Compose. Este documento describe una propuesta de despliegue en AWS manteniendo la arquitectura desarrollada y separando los servicios públicos de la infraestructura interna.

## 2. Arquitectura propuesta

Una primera migración cloud puede utilizar:

- Amazon EC2 para ejecutar los contenedores.
- Amazon RDS for PostgreSQL para persistencia administrada.
- Amazon MSK como evolución del broker Kafka local.
- AWS Secrets Manager para credenciales y secretos.
- Application Load Balancer para exposición controlada de los canales.

Config Server, Eureka, PostgreSQL, Kafka y los microservicios de negocio deben permanecer en red privada.

## 3. Preparación

Crear una instancia EC2 e instalar:

- Git
- Docker
- Docker Compose

Clonar la entrega:

    git clone https://github.com/mzunigacc/bank-legacy-migration.git
    cd bank-legacy-migration
    
Configurar las direcciones y credenciales del ambiente mediante variables de entorno. Los secretos productivos no deben almacenarse en el repositorio.

## 4. Construcción y ejecución

Construir las imágenes:

    docker compose build

Levantar los componentes:

    docker compose up -d

Verificar el estado:

    docker compose ps

Los health checks de Account, Payment y Customer Service permiten comprobar su disponibilidad.

## 5. Persistencia y mensajería

PostgreSQL puede migrarse a Amazon RDS modificando la configuración de conexión mediante variables de entorno.

Kafka puede migrarse a Amazon MSK manteniendo los tópicos:

- `bank.withdrawals`
- `bank.payments`

Los bootstrap servers se configuran externamente sin modificar la lógica de negocio.

## 6. Seguridad

Los BFF mantienen HTTPS y OAuth2/JWT como mecanismos de acceso.

En producción:

- sólo los puntos de entrada requeridos deben exponerse públicamente;
- los servicios internos deben permanecer en red privada;
- credenciales y secretos deben administrarse mediante AWS Secrets Manager;
- las reglas de Security Group deben limitar los puertos según el origen requerido.

## 7. Escalabilidad

Account, Payment y Customer Service pueden ejecutarse con múltiples réplicas.

La implementación actual fue validada mediante escalamiento horizontal con Docker Compose. En AWS puede evolucionarse hacia Amazon ECS con políticas de autoscaling y balanceo de carga.

## 8. Evolución productiva

Como siguientes pasos se propone:

- persistir los clientes registrados del Authorization Server;
- incorporar Authorization Code con PKCE para usuarios finales;
- aplicar un patrón Outbox para coordinar persistencia y publicación de eventos;
- evolucionar hacia persistencia independiente por microservicio;
- incorporar métricas, trazas y alertas.
