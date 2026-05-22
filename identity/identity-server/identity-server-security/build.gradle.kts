plugins {
    id("seatliberator.layer.security")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity:identity-server:identity-server-api"))
    implementation(project(":identity:identity-server:identity-server-application"))
    implementation(project(":identity:identity-server:identity-server-domain"))
    implementation(project(":identity:identity-core"))

    implementation(libs.spring.boot.starter.oauth2.client)
    implementation(libs.tools.jackson.databind)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}