plugins {
    id("seatliberator.spring-identity-application")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    // API
    implementation(project(":identity:identity-api"))

    // External API
    implementation(project(":reservation:reservation-api"))
    implementation(project(":board:board-api"))
}