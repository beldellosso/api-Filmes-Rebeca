# 1. ESTÁGIO DE BUILD
# MUDANÇA CRUCIAL: Usando JDK 21 para compilar para o Java 21
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia pom.xml primeiro para usar o cache de dependências
COPY pom.xml .

# Copia os scripts e o arquivo de configuração do Maven Wrapper
COPY mvnw mvnw.cmd .
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/

# Copia o código-fonte restante
COPY src /app/src

# Constrói o projeto Quarkus
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# 2. ESTÁGIO DE EXECUÇÃO
# MUDANÇA CRUCIAL: Usando JRE 21 para garantir compatibilidade
FROM eclipse-temurin:21-jre-alpine
WORKDIR /work/

# Copia a aplicação construída
COPY --from=build /app/target/quarkus-app/lib /work/lib
COPY --from=build /app/target/quarkus-app/*.jar /work/
COPY --from=build /app/target/quarkus-app/app/ /work/app

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]