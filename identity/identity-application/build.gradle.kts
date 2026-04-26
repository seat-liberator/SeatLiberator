plugins {
    id("seatliberator.spring-boot-monolith-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    implementation(project(":identity:identity-api"))

    implementation(project(":reservation:reservation-api"))
    implementation(project(":board:board-api"))

    implementation(libs.spring.boot.starter.kafka)
    implementation(project(":event-relay:event-relay-starter"))
    implementation(project(":bootstrap:application-starter"))
}