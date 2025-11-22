## Stage 1: Build da aplicação com Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do Maven primeiro (para cache de dependências)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Faz o wrapper executável
RUN chmod +x ./mvnw

# Baixa as dependências (camada cacheável)
RUN ./mvnw dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Compila a aplicação
RUN ./mvnw package -DskipTests -Dquarkus.package.jar.type=uber-jar

## Stage 2: Imagem final de runtime
FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.18

# Variáveis de ambiente
ENV LANGUAGE='en_US:en'
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Copia o uber-jar da stage de build
COPY --from=build --chown=185 /app/target/*-runner.jar /deployments/quarkus-run.jar

# Expõe a porta
EXPOSE 8080

# Define o usuário
USER 185

# Comando de execução
ENTRYPOINT [ "java", "-Dquarkus.http.host=0.0.0.0", "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", "-jar", "/deployments/quarkus-run.jar" ]