FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml /app/pom.xml
COPY shared /app/shared
COPY services /app/services

RUN mvn -pl services/order-service -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/services/order-service/target/order-service-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
