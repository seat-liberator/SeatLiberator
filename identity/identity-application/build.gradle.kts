plugins {
    id("seatliberator.layer.launcher")
    id("seatliberator.layer.persistence")
    id("seatliberator.layer.webmvc")
    id("seatliberator.layer.security")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity:identity-api"))
    implementation(project(":identity:identity-server:identity-server-domain"))
    implementation(project(":reservation:reservation-api"))
    implementation(project(":board:board-api"))
    implementation(project(":event-relay:event-relay-starter"))
    implementation(project(":bootstrap:application-starter"))

    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.spring.boot.starter.kafka)
}