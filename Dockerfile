# 1. ESTÁGIO DE BUILD
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia pom.xml primeiro para usar o cache de dependências
COPY pom.xml .

# Copia os scripts do Maven Wrapper (mvnw e mvnw.cmd)
# e o arquivo de configuração (maven-wrapper.properties)
# O arquivo binário 'maven-wrapper.jar' NÃO é copiado.
COPY mvnw mvnw.cmd .
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/

# Copia o código-fonte restante
COPY src /app/src

# Constrói o projeto Quarkus
# O comando ./mvnw agora fará o download do maven-wrapper.jar antes de rodar o pacote.
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# 2. ESTÁGIO DE EXECUÇÃO
FROM eclipse-temurin:17-jre-alpine
WORKDIR /work/

# Copia a aplicação construída
COPY --from=build /app/target/quarkus-app/lib /work/lib
COPY --from=build /app/target/quarkus-app/*.jar /work/
COPY --from=build /app/target/quarkus-app/app/ /work/app

EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]