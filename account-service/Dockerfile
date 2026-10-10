# =========================================================
# ETAPA 1 - BUILD
# =========================================================
FROM maven:3.9.11-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B


# =========================================================
# ETAPA 2 - RUNTIME
# =========================================================
FROM eclipse-temurin:17-jre

LABEL org.opencontainers.image.title="Banco XYZ EFT Microservice"
LABEL org.opencontainers.image.description="Microservicio Spring Boot para modernización Banco XYZ"

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring spring

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

RUN chown spring:spring /app/app.jar

USER spring:spring

HEALTHCHECK --interval=30s --timeout=5s --start-period=120s --retries=3 \
    CMD curl --fail --silent http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
