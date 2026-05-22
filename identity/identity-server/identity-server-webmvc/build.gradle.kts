plugins {
    id("seatliberator.layer.launcher")
    id("seatliberator.layer.webmvc")
    id("seatliberator.spring.security")
}

group = "com.seatliberator.seatliberator"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(project(":identity:identity-server:identity-server-persistence"))
    implementation(project(":identity:identity-server:identity-server-application"))
    implementation(project(":identity:identity-server:identity-server-domain"))
    implementation(project(":identity:identity-core"))

    implementation(libs.nimbus.jose.jwt)
}