# 1. ESTÁGIO DE BUILD (Compilação)
# Usa uma imagem Maven/JDK para construir o projeto
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia o pom.xml e o código-fonte
COPY pom.xml .
COPY src /app/src

# Constrói o projeto Quarkus e gera o JAR executável
# Utilizamos o -DskipTests para pular testes e acelerar o deploy
RUN mvn clean package -DskipTests

# 2. ESTÁGIO DE EXECUÇÃO
# Usa uma imagem JRE (Java Runtime Environment) menor e mais segura para produção
FROM eclipse-temurin:17-jre-alpine
WORKDIR /work/

# Copia apenas o resultado da compilação (o JAR executável e as dependências)
COPY --from=build /app/target/quarkus-app/lib /work/lib
COPY --from=build /app/target/quarkus-app/*.jar /work/
COPY --from=build /app/target/quarkus-app/app /work/app

# Define o ponto de entrada (ENTRYPOINT) e o comando (CMD) para rodar o JAR
# O -Dquarkus.http.host=0.0.0.0 garante que o Quarkus escute em todas as interfaces,
# o que é necessário em ambientes de container como o Render.
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]
CMD [ "-Dquarkus.http.host=0.0.0.0", "quarkus-run.jar" ]