FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd -r -u 10001 -g root appuser

COPY --from=build /app/build/libs/*.jar app.jar

RUN chown -R appuser:root /app && chmod 500 /app/app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]