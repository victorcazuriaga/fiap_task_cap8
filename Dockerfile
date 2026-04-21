# syntax=docker/dockerfile:1.7

# ===== Stage 1: build =====
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies separately from source for faster rebuilds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests \
 && mv target/*.jar target/app.jar

# ===== Stage 2: runtime =====
FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app

# Non-root user
RUN groupadd --system --gid 1001 spring \
 && useradd  --system --uid 1001 --gid spring --shell /sbin/nologin spring

# curl for HEALTHCHECK
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

COPY --from=builder --chown=spring:spring /app/target/app.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

USER spring
EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=120s --retries=5 \
  CMD curl -fsS http://localhost:${SERVER_PORT}/actuator/health/liveness || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
