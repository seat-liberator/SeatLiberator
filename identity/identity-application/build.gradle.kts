plugins {
    id("seatliberator.web-application")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    // Web Security
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // API
    implementation(project(":identity:identity-api"))

    // External API
    implementation(project(":reservation:reservation-api"))
    implementation(project(":board:board-api"))
}