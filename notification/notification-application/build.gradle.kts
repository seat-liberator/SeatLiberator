plugins {
    id("seatliberator.spring-boot-monolith-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":notification:notification-api"))

    implementation(project(":bootstrap:application-starter"))
    implementation(project(":bootstrap:resource-application-starter"))
}