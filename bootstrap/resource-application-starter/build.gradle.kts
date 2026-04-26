plugins {
    id("seatliberator.web-mvc-base")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    api(project(":identity:identity-client-starter"))
    api(project(":event-relay:event-relay-starter"))

    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.oauth2.resource.server)
    api(libs.spring.boot.starter.validation)

    implementation(libs.spring.boot.starter.kafka)
}
