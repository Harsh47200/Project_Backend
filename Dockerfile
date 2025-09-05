# Build stage
FROM maven:3.8.3-openjdk-17 AS build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Package stage
FROM eclipse-temurin:17-jdk
COPY --from=build /target/jcp-api.jar demo.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","demo.jar"]
