# 1. ESTÁGIO DE BUILD
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia pom.xml primeiro (para cache de dependências)
COPY pom.xml .

# Copia o Maven Wrapper e seu arquivo de configuração
# Usamos o wildcard (*) para garantir que o CONTEÚDO da pasta .mvn/wrapper
# seja copiado, caso a plataforma tenha dificuldade com pastas ocultas.
# O Render ou o Docker não precisa da pasta .mvn em si, mas sim do wrapper.
COPY mvnw mvnw.cmd .
COPY .mvn/wrapper/maven-wrapper.properties .mvn/wrapper/maven-wrapper.jar /app/.mvn/wrapper/

# Copia o código-fonte restante
COPY src /app/src

# Constrói o projeto Quarkus
RUN chmod +x ./mvnw
# O erro "cannot open /app/.mvn/wrapper/maven-wrapper.properties" será resolvido
# porque o arquivo agora está no local esperado (/app/.mvn/wrapper/)
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