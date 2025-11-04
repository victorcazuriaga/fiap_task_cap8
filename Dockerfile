# Etapa 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copia o código do projeto
COPY pom.xml .
COPY src ./src

# Compila o projeto e gera o jar
RUN mvn clean package -DskipTests

# Etapa 2: Executa a aplicação
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia o JAR gerado da etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]