FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /app

# Copia somente o pom e baixa dependências (cache)
COPY pom.xml .

RUN mvn -ntp dependency:go-offline

# Copia o código
COPY src ./src

# Compila o projeto Quarkus
RUN mvn clean package -DskipTests -Dquarkus.package.jar.type=uber-jar

# ------------------------------
# Imagem final
# ------------------------------
FROM amazoncorretto:21

WORKDIR /app

COPY --from=build /app/target/*-runner.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
