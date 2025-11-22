# 1. ESTÁGIO DE BUILD (Multi-Stage Build)
# Usa a imagem JDK 17 para compilar o código
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia o pom.xml e o código-fonte
COPY pom.xml .
COPY src /app/src

# Constrói o projeto Quarkus
# Usamos o comando "mvn package -DskipTests"
# -DskipTests é crucial para builds rápidos
RUN chmod +x ./mvnw
RUN ./mvnw package -DskipTests

# 2. ESTÁGIO DE EXECUÇÃO
# Usa uma imagem JRE (Java Runtime Environment) menor para rodar a aplicação
FROM eclipse-temurin:17-jre-alpine
WORKDIR /work/

# Copia apenas o resultado da compilação do ESTÁGIO DE BUILD
COPY --from=build /app/target/quarkus-app/lib /work/lib
COPY --from=build /app/target/quarkus-app/*.jar /work/
COPY --from=build /app/target/quarkus-app/app/ /work/app

# Expõe a porta 8080 (padrão do Quarkus)
EXPOSE 8080

# Comando para iniciar o Quarkus (corrigido para usar o nome de arquivo correto)
ENTRYPOINT [ "java", "-jar", "quarkus-run.jar" ]