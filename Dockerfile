# ------------------------------------------------------------------------
# 1. STAGE DE BUILD (COMPILAÇÃO)
# Usa Maven com JDK 21 para suportar a compilação do projeto Java 21
# ------------------------------------------------------------------------
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro para usar o cache de dependências do Maven
COPY pom.xml .

# ====================================================================
# PASSO DE CORREÇÃO CRÍTICO (Resolve o erro Jandex no Build)
# Remove os caches de Jandex e Quarkus que estão corrompidos, forçando
# o Maven a baixá-los e processá-los corretamente no passo seguinte.
# ====================================================================
RUN rm -rf /root/.m2/repository/io/quarkus \
    && rm -rf /root/.m2/repository/org/jboss/jandex

# Baixa as dependências. O || true garante que o build não falhe se o go-offline retornar erro (comum em caches vazios)
RUN mvn dependency:go-offline -B || true

# Copia todo o código fonte
COPY src ./src

# Compila a aplicação. Cria um uber-jar (-runner.jar).
# Este passo deve funcionar agora que os caches foram limpos.
RUN mvn clean package -DskipTests -Dquarkus.package.jar.type=uber-jar

# ------------------------------------------------------------------------
# 2. STAGE DE RUNTIME (EXECUÇÃO)
# Usa uma imagem JRE 21 mais leve para executar a aplicação
# ------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine

# Variáveis de ambiente
ENV LANGUAGE='en_US:en'
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dquarkus.http.host=0.0.0.0"

# Define o diretório de trabalho
WORKDIR /deployments/

# Copia o JAR compilado do estágio de build
COPY --from=build --chown=1000:1000 /app/target/*-runner.jar ./quarkus-run.jar

# Expõe a porta padrão do Quarkus
EXPOSE 8080

# Usuário não-root (1000 é o padrão do Temurin Alpine)
USER 1000

# Execução da aplicação
CMD ["java", "-jar", "quarkus-run.jar"]