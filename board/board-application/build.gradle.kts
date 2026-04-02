plugins {
    id("seatliberator.resource-application")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    // API
    implementation(project(":board:board-api"))
}