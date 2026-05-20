plugins {
    id("seatliberator.spring-boot-monolith-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":board:board-api"))
    implementation(project(":board:board-domain"))
    implementation(project(":bootstrap:application-starter"))
    implementation(project(":bootstrap:resource-application-starter"))
}