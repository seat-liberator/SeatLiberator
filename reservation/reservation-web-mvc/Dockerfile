FROM gradle:8.14.4-jdk21 AS builder
WORKDIR /app

COPY . .

RUN gradle :reservation:reservation-web-mvc:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/reservation/reservation-web-mvc/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
