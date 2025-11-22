## Stage 1: Build da aplicação com Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Define o diretório de trabalho
WORKDIR /build

# Copia os arquivos do projeto
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Garante permissão de execução no wrapper
RUN chmod +x mvnw

# Copia o código fonte
COPY src ./src

# Compila a aplicação diretamente (sem step de cache separado)
# Usa -DskipTests para acelerar o build
RUN ./mvnw clean package -DskipTests -Dquarkus.package.jar.type=uber-jar

## Stage 2: Imagem final de runtime
FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.18

# Metadados
LABEL maintainer="Senac TSI"
LABEL description="API de Filmes - Rebeca"

# Variáveis de ambiente
ENV LANGUAGE='en_US:en'
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Copia o uber-jar da stage de build
COPY --from=build --chown=185 /build/target/*-runner.jar /deployments/quarkus-run.jar

# Expõe a porta
EXPOSE 8080

# Define o usuário não-root
USER 185

# Comando de execução
ENTRYPOINT ["java", "-Dquarkus.http.host=0.0.0.0", "-Dquarkus.http.port=${PORT:-8080}", "-jar", "/deployments/quarkus-run.jar"]