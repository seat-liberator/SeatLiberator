FROM gradle:8.14.4-jdk21 AS builder
WORKDIR /app

COPY . .

RUN gradle :reservation:reservation-application:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/reservation/reservation-application/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
