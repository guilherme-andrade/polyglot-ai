# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle gradle
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true
COPY src src
COPY config config
RUN ./gradlew --no-daemon clean bootJar -x test -x spotlessCheck -x checkstyleMain -x checkstyleTest

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system polyglot && useradd --system --gid polyglot polyglot
COPY --from=build /workspace/build/libs/*.jar app.jar
USER polyglot
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "/app/app.jar"]
