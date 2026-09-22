# Propuesta Técnica -- Semana 6

**Asignatura:** Desarrollo Backend III (PBY2203)\
**Proyecto:** Bank Legacy Migration\
**Estudiante:** Matías Zúñiga

## 1. Objetivo

Evolucionar la solución bancaria desarrollada durante las semanas
anteriores hacia una arquitectura de microservicios con componentes de
Spring Cloud, incorporando configuración centralizada, descubrimiento de
servicios, tolerancia a fallos y mecanismos de autenticación y
autorización.

## 2. Arquitectura propuesta

La solución mantiene a **Bank Core** como responsable de la lógica de
negocio y persistencia en PostgreSQL, mientras que **BFF Web, BFF Mobile
y BFF ATM** exponen APIs específicas para cada canal.

Se incorporan:

-   **Spring Cloud Config Server:** centraliza propiedades utilizadas
    por los BFF.
-   **Eureka Discovery Server:** registra y permite visualizar los tres
    BFF.
-   **Resilience4j Circuit Breaker:** protege las consultas de los BFF
    hacia Bank Core y permite respuestas controladas ante
    indisponibilidad.
-   **JWT + HTTPS:** mantiene autenticación y autorización diferenciada
    por canal.

## 3. Componentes

  Componente         Responsabilidad
  ------------------ -------------------------------------------------
  Config Server      Centralizar configuración de los microservicios
  Discovery Server   Registrar y descubrir servicios mediante Eureka
  BFF Web            Exponer operaciones del canal web
  BFF Mobile         Exponer operaciones del canal móvil
  BFF ATM            Exponer operaciones del canal ATM
  Bank Core          Concentrar lógica de negocio y acceso a datos
  PostgreSQL         Persistencia de información bancaria

## 4. Seguridad y resiliencia

Cada BFF utiliza **JWT** para autenticar solicitudes y roles específicos
(`ROLE_WEB`, `ROLE_MOBILE` y `ROLE_ATM`) para autorizar el acceso según
el canal. Los BFF exponen sus APIs mediante **HTTPS**.

Las consultas hacia Bank Core incorporan **Circuit Breaker con
Resilience4j**. Si Bank Core no está disponible, los métodos protegidos
ejecutan un fallback y evitan propagar directamente el error de
conexión.

En operaciones transaccionales, como el retiro ATM, se conservan las
reglas y excepciones del dominio para evitar simular una operación
exitosa mediante un fallback.

## 5. Configuración y descubrimiento

Los BFF consumen configuración desde Config Server mediante
`spring.config.import`. El repositorio de configuración contiene las
propiedades específicas de cada aplicación.

Los tres BFF se registran en Eureka como `BFF-WEB`, `BFF-MOBILE` y
`BFF-ATM`, permitiendo comprobar centralmente el estado de las
instancias.

## 6. Validación realizada

La implementación fue validada mediante ejecución real y pruebas por
consola/API:

-   Config Server entrega correctamente la configuración de BFF Web.
-   Eureka registra simultáneamente los tres BFF en estado `UP`.
-   Los tres BFF responden mediante HTTPS y JWT cuando Bank Core está
    disponible.
-   JWT con rol correcto: HTTP `200`.
-   Solicitud sin JWT: HTTP `401`.
-   JWT correspondiente a otro canal: HTTP `403`.
-   Con Bank Core detenido, los tres BFF ejecutan el fallback de
    Resilience4j y entregan una respuesta controlada.

Las capturas se encuentran en `evidencias_ejecucion/semana6/`.

## 7. Conclusión

La propuesta extiende la arquitectura existente sin reemplazar las
responsabilidades ya implementadas. Spring Cloud Config, Eureka y
Resilience4j permiten centralizar configuración, registrar servicios y
manejar fallos de comunicación, mientras JWT y HTTPS mantienen la
seguridad por canal.

La solución queda preparada para continuar evolucionando hacia una
arquitectura distribuida con mayores capacidades de escalabilidad,
observabilidad y resiliencia.
