plugins {
    id("java")
    id("java-library")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":bootstrap:web-application-starter"))

    // Identity
    api(project(":identity:identity-client"))
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}
