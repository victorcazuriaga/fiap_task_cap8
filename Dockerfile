# Etapa 1: Build da aplicação
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copia apenas o POM primeiro para aproveitar cache de dependências
COPY pom.xml ./

# Baixa dependências offline (melhora cache em camadas)
RUN mvn -B -DskipTests dependency:go-offline

# Copia o código fonte e compila
COPY src ./src
RUN mvn -B -DskipTests package

# Etapa 2: Executa a aplicação
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia o JAR gerado da etapa anterior
COPY --from=builder /app/target/*.jar /app/app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]