plugins {
    id("seatliberator.resource-application")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    // API
    implementation(project(":reservation:reservation-api"))

    // External API
    implementation(project(":notification:notification-api"))
}