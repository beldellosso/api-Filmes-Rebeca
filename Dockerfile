FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace
COPY . .

RUN chmod +x mvnw
RUN ./mvnw package -DskipTests -Dquarkus.package.type=uber-jar

FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY --from=build /workspace/target/*-runner.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
