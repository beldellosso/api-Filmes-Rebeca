FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw package -DskipTests -Dquarkus.package.type=uber-jar

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*-runner.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
