# Build the Quarkus application with Java 21.
FROM gradle:8.10.2-jdk21 AS build

WORKDIR /workspace
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY build.gradle settings.gradle gradle.properties ./
COPY src src

RUN chmod +x gradlew && ./gradlew build --no-daemon -x test

# Run the fast-jar distribution produced by Quarkus.
FROM eclipse-temurin:21-jre

WORKDIR /work
COPY --from=build /workspace/build/quarkus-app/ ./

EXPOSE 34000
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
